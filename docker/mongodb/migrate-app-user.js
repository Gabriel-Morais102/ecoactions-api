// Migração idempotente para volumes existentes. Não remove collections nem documentos.
// Executar com mongosh dentro do container MongoDB após atualizar suas variáveis de ambiente.
const adminDb = db.getSiblingDB("admin");
const authentication = adminDb.auth(
    process.env.MONGO_INITDB_ROOT_USERNAME,
    process.env.MONGO_INITDB_ROOT_PASSWORD
);

if (authentication !== 1 && authentication?.ok !== 1) {
    throw new Error("Falha na autenticação administrativa para configurar o usuário da aplicação.");
}

load("/docker-entrypoint-initdb.d/02-create-app-user.js");
