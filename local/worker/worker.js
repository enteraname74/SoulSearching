import initSqlJs from 'sql.js';

let SQL = null;

// Maps to track active database connections and prepared statements by their unique IDs.
const databases = new Map(); // stores databaseId -> SQL.Database
const statements = new Map(); // stores statementId -> SQL.Statement

// Counters to generate unique IDs for new database connections and statements.
let nextDatabaseId = 0;
let nextStatementId = 0;

function openRequest(id, requestData) {
    try {
        const newDatabaseId = nextDatabaseId++;

        // sql.js operates in-memory by default. If you want to load from requestData.fileName,
        // you would normally fetch the file as an ArrayBuffer and pass it here:
        // new SQL.Database(new Uint8Array(buffer));
        const newDatabase = new SQL.Database();
        databases.set(newDatabaseId, newDatabase);
        postMessage({'id': id, data: {'databaseId': newDatabaseId}});
    } catch (error) {
        postMessage({'id': id, error: error.message});
    }
}

function prepareRequest(id, requestData) {
    try {
        const newStatementId = nextStatementId++;
        const database = databases.get(requestData.databaseId);
        if (!database) {
            postMessage({'id': id, error: "Invalid database ID: " + requestData.databaseId});
            return;
        }
        const statement = database.prepare(requestData.sql);
        statements.set(newStatementId, statement);

        const resultData = {
            'statementId': newStatementId,
            // sql.js doesn't expose bind_parameter_count easily, defaulting to a big number
            'parameterCount': 256,
            'columnNames': statement.getColumnNames()
        };

        postMessage({'id': id, data: resultData});
    } catch (error) {
        postMessage({'id': id, error: error.message});
    }
}

function stepRequest(id, requestData) {
    const statement = statements.get(requestData.statementId);
    if (!statement) {
        postMessage({'id': id, error: "Invalid statement ID: " + requestData.statementId});
        return;
    }

    try {
        const resultData = {
            'rows': [],
            'columnTypes': []
        };

        // Reset the statement state so we can bind new parameters and execute again
        statement.reset();

        if (requestData.bindings && requestData.bindings.length > 0) {
            statement.bind(requestData.bindings);
        }

        while (statement.step()) {
            const row = statement.get();

            // sql.js doesn't expose sqlite3_column_type. We infer the types
            // from the first row's JavaScript values.
            // 1=INTEGER, 2=FLOAT, 3=TEXT, 4=BLOB, 5=NULL
            if (resultData.columnTypes.length === 0) {
                for (let i = 0; i < row.length; i++) {
                    const val = row[i];
                    if (val === null) {
                        resultData.columnTypes.push(5);
                    } else if (typeof val === 'number') {
                        resultData.columnTypes.push(Number.isInteger(val) ? 1 : 2);
                    } else if (typeof val === 'string') {
                        resultData.columnTypes.push(3);
                    } else if (val instanceof Uint8Array) {
                        resultData.columnTypes.push(4);
                    } else {
                        resultData.columnTypes.push(5); // fallback
                    }
                }
            }

            resultData.rows.push(row);
        }

        postMessage({'id': id, data: resultData});
    } catch (error) {
        postMessage({'id': id, error: error.message});
    }
}

function closeRequest(id, requestData) {
    if (requestData.statementId !== undefined && requestData.statementId != null) {
        const statement = statements.get(requestData.statementId);
        if (statement == null) {
            postMessage({'id': id, error: "Invalid statement ID: " + requestData.statementId});
            return;
        }
        try {
            statement.free(); // sql.js uses free() instead of finalize()
            statements.delete(requestData.statementId);
        } catch (error) {
            postMessage({'id': id, error: error.message});
        }
    }

    if (requestData.databaseId !== undefined && requestData.databaseId != null) {
        const database = databases.get(requestData.databaseId);
        if (database == null) {
            postMessage({'id': id, error: "Invalid database ID: " + requestData.databaseId});
            return;
        }
        try {
            database.close();
            databases.delete(requestData.databaseId);
        } catch (error) {
            postMessage({'id': id, error: error.message});
        }
    }
}

// A map that links command names (strings) to their respective handler functions.
const commandMap = {
    'open': openRequest,
    'prepare': prepareRequest,
    'step': stepRequest,
    'close': closeRequest,
};

function handleMessage(e) {
    const requestMsg = e.data;
    console.log("handleMessage: " + JSON.stringify(requestMsg));
    if (!Object.hasOwn(requestMsg, 'data') && requestMsg.data == null) {
        postMessage({'id': requestMsg.id, 'error': "Invalid request, missing 'data'."});
        return;
    }
    if (!Object.hasOwn(requestMsg.data, 'cmd') && requestMsg.data.cmd == null) {
        postMessage({'id': requestMsg.id, 'error': "Invalid request, missing 'cmd'."});
        return;
    }
    const command = requestMsg.data.cmd;
    const requestHandler = commandMap[command];
    if (requestHandler) {
        requestHandler(requestMsg.id, requestMsg.data);
    } else {
        postMessage({'id': requestMsg.id, 'error': "Invalid request, unknown command: '" + command + "'."});
    }
}

// Queue messages that arrive before sql.js has finished initializing
const messageQueue = [];
onmessage = (e) => {
    if (!SQL) {
        messageQueue.push(e);
    } else {
        handleMessage(e);
    }
};

// Initialize sql.js. Depending on your build system, you may need to specify
// the `locateFile` option to point to the sql-wasm.wasm file.
initSqlJs({
    locateFile: file => 'https://cdnjs.cloudflare.com/ajax/libs/sql.js/1.13.0/sql-wasm.wasm'
}).then(instance => {
    SQL = instance;
    while (messageQueue.length > 0) {
        handleMessage(messageQueue.shift());
    }
});