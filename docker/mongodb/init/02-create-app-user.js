// Executado automaticamente na primeira inicialização do volume.
// A migração de volumes existentes carrega somente este arquivo, sem repetir a carga de dados.
const databaseName = process.env.MONGO_INITDB_DATABASE;
const username = process.env.MONGO_APP_USERNAME;
const password = process.env.MONGO_APP_PASSWORD;

if (!databaseName || !username || !password) {
    throw new Error("Defina MONGO_INITDB_DATABASE, MONGO_APP_USERNAME e MONGO_APP_PASSWORD.");
}

if (["admin", "local", "config"].includes(databaseName)) {
    throw new Error("Use um banco específico da aplicação, diferente de admin, local e config.");
}

const applicationDb = db.getSiblingDB(databaseName);
const credentials = {
    pwd: password,
    roles: [{ role: "readWrite", db: databaseName }]
};

if (applicationDb.getUser(username)) {
    applicationDb.updateUser(username, credentials);
} else {
    applicationDb.createUser({ user: username, ...credentials });
}

print("Usuário da aplicação configurado com acesso readWrite ao banco " + databaseName + ".");
