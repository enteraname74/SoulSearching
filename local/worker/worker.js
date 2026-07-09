import sqlite3InitModule from "@sqlite.org/sqlite-wasm";

let sqlite3 = null;

const databases = new Map(); // databaseId -> sqlite3.oo1.DB
const statements = new Map(); // statementId -> sqlite3.oo1.Stmt
const statementDatabases = new Map(); // statementId -> databaseId

let nextDatabaseId = 0;
let nextStatementId = 0;

const messageQueue = [];

function postError(id, error) {
    postMessage({
        id,
        error: error instanceof Error ? error.message : String(error),
    });
}

function openRequest(id, requestData) {
    try {
        if (!sqlite3.oo1.OpfsDb) {
            postError(id, "OPFS is not available. Check browser support and COOP/COEP headers.");
            return;
        }

        const databaseId = nextDatabaseId++;

        // Room may send fileName. If not, use a stable default name.
        const fileName = requestData.fileName || "/SoulSearching.db";

        // "c" means create if needed.
        // This database persists in OPFS across page reloads.
        const database = new sqlite3.oo1.OpfsDb(fileName, "c");

        databases.set(databaseId, database);

        postMessage({
            id,
            data: {
                databaseId,
            },
        });
    } catch (error) {
        postError(id, error);
    }
}

function prepareRequest(id, requestData) {
    try {
        const database = databases.get(requestData.databaseId);

        if (!database) {
            postError(id, "Invalid database ID: " + requestData.databaseId);
            return;
        }

        const statementId = nextStatementId++;
        const statement = database.prepare(requestData.sql);

        statements.set(statementId, statement);
        statementDatabases.set(statementId, requestData.databaseId);

        const columnNames = [];
        const columnCount = statement.columnCount;

        for (let i = 0; i < columnCount; i++) {
            columnNames.push(statement.getColumnName(i));
        }

        postMessage({
            id,
            data: {
                statementId,
                // Some APIs expose parameterCount, but keep a safe fallback
                // matching your previous worker behavior.
                parameterCount: statement.parameterCount || 256,
                columnNames,
            },
        });
    } catch (error) {
        postError(id, error);
    }
}

function bindStatement(statement, bindings) {
    if (!bindings || bindings.length === 0) return;

    // Room sends an array of bind values.
    // SQLite WASM supports array binding.
    statement.bind(bindings);
}

function getColumnTypes(statement) {
    const columnTypes = [];

    for (let i = 0; i < statement.columnCount; i++) {
        // SQLite column type constants:
        // SQLITE_INTEGER = 1
        // SQLITE_FLOAT   = 2
        // SQLITE_TEXT    = 3
        // SQLITE_BLOB    = 4
        // SQLITE_NULL    = 5
        columnTypes.push(
            sqlite3.capi.sqlite3_column_type(statement.pointer, i)
        );
    }

    return columnTypes;
}

function stepRequest(id, requestData) {
    const statement = statements.get(requestData.statementId);

    if (!statement) {
        postError(id, "Invalid statement ID: " + requestData.statementId);
        return;
    }

    try {
        const resultData = {
            rows: [],
            columnTypes: [],
        };

        statement.reset();
        statement.clearBindings();

        bindStatement(statement, requestData.bindings);

        while (statement.step()) {
            if (resultData.columnTypes.length === 0) {
                resultData.columnTypes = getColumnTypes(statement);
            }

            // [] means return the row as an array.
            const row = statement.get([]);
            resultData.rows.push(row);
        }

        postMessage({
            id,
            data: resultData,
        });
    } catch (error) {
        postError(id, error);
    }
}

function closeRequest(id, requestData) {
    if (requestData.statementId !== undefined && requestData.statementId !== null) {
        const statement = statements.get(requestData.statementId);

        if (!statement) {
            postError(id, "Invalid statement ID: " + requestData.statementId);
            return;
        }

        try {
            statement.finalize();
            statements.delete(requestData.statementId);
            statementDatabases.delete(requestData.statementId);
        } catch (error) {
            postError(id, error);
            return;
        }
    }

    if (requestData.databaseId !== undefined && requestData.databaseId !== null) {
        const database = databases.get(requestData.databaseId);

        if (!database) {
            postError(id, "Invalid database ID: " + requestData.databaseId);
            return;
        }

        try {
            database.close();
            databases.delete(requestData.databaseId);
        } catch (error) {
            postError(id, error);
            return;
        }
    }

    // Important for Room:
    // Do NOT post a success response for close.
}

const commandMap = {
    open: openRequest,
    prepare: prepareRequest,
    step: stepRequest,
    close: closeRequest,
};

function handleMessage(e) {
    const requestMsg = e.data;

    if (!requestMsg || !requestMsg.data) {
        postError(requestMsg?.id, "Invalid request, missing 'data'.");
        return;
    }

    const command = requestMsg.data.cmd;

    if (!command) {
        postError(requestMsg.id, "Invalid request, missing 'cmd'.");
        return;
    }

    const requestHandler = commandMap[command];

    if (!requestHandler) {
        postError(requestMsg.id, "Invalid request, unknown command: '" + command + "'.");
        return;
    }

    requestHandler(requestMsg.id, requestMsg.data);
}

onmessage = (e) => {
    if (!sqlite3) {
        messageQueue.push(e);
    } else {
        handleMessage(e);
    }
};

sqlite3InitModule({
    print: console.log,
    printErr: console.error,
}).then((instance) => {
    sqlite3 = instance;

    if (!sqlite3.oo1.OpfsDb) {
        console.warn("SQLite OPFS is not available in this browser/context.");
    }

    while (messageQueue.length > 0) {
        handleMessage(messageQueue.shift());
    }
}).catch((error) => {
    console.error("Failed to initialize SQLite WASM:", error);
});