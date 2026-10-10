/*
 * EcoActions - criação das collections, carga inicial e demonstração de CRUD.
 *
 * Este arquivo é executado automaticamente pelo container oficial do MongoDB
 * somente na primeira criação do volume de dados. Ele também pode ser executado
 * manualmente no mongosh para repetir a demonstração em um ambiente local.
 */

// A imagem oficial do MongoDB abre o banco definido em MONGO_INITDB_DATABASE
// antes de executar este arquivo. Assim, a mesma carga funciona em local,
// staging e produção sem fixar o nome do banco no código.

const collectionNames = [
    "alertas",
    "destinacoes",
    "coletas",
    "pontos_coleta",
    "residuos"
];

// Permite repetir o roteiro de demonstração sem gerar documentos duplicados.
collectionNames.forEach((collectionName) => {
    if (db.getCollectionNames().includes(collectionName)) {
        db.getCollection(collectionName).drop();
    }
});

print("=== CREATE: criação das cinco collections ===");

db.createCollection("residuos", {
    validator: {
        $jsonSchema: {
            bsonType: "object",
            required: ["tipoResiduo", "limiteMaximo"],
            properties: {
                tipoResiduo: {
                    bsonType: "string",
                    minLength: 2,
                    maxLength: 50,
                    description: "Tipo do material monitorado"
                },
                limiteMaximo: {
                    bsonType: "int",
                    minimum: 1,
                    description: "Limite de referência em quilogramas"
                }
            }
        }
    },
    validationLevel: "moderate",
    validationAction: "error"
});

db.createCollection("pontos_coleta", {
    validator: {
        $jsonSchema: {
            bsonType: "object",
            required: ["residuo", "local", "capacidadeAtual", "capacidadeMaxima"],
            properties: {
                residuo: { bsonType: "objectId" },
                local: { bsonType: "string", minLength: 3, maxLength: 100 },
                capacidadeAtual: { bsonType: "int", minimum: 0 },
                capacidadeMaxima: { bsonType: "int", minimum: 1 }
            }
        }
    },
    validationLevel: "moderate",
    validationAction: "error"
});

db.createCollection("coletas", {
    validator: {
        $jsonSchema: {
            bsonType: "object",
            required: ["pontoColeta", "dataColeta", "quantidadeColeta"],
            properties: {
                pontoColeta: { bsonType: "objectId" },
                dataColeta: { bsonType: "date" },
                quantidadeColeta: { bsonType: "int", minimum: 1 }
            }
        }
    },
    validationLevel: "moderate",
    validationAction: "error"
});

db.createCollection("destinacoes", {
    validator: {
        $jsonSchema: {
            bsonType: "object",
            required: ["coleta", "tipoDestino"],
            properties: {
                coleta: { bsonType: "objectId" },
                tipoDestino: { bsonType: "string", minLength: 3, maxLength: 50 }
            }
        }
    },
    validationLevel: "moderate",
    validationAction: "error"
});

db.createCollection("alertas", {
    validator: {
        $jsonSchema: {
            bsonType: "object",
            required: ["mensagem", "dataAlerta"],
            properties: {
                mensagem: { bsonType: "string", minLength: 5, maxLength: 200 },
                dataAlerta: { bsonType: "date" }
            }
        }
    },
    validationLevel: "moderate",
    validationAction: "error"
});

printjson({ collectionsCriadas: db.getCollectionNames().sort() });

print("=== INDEXES: criação de índices de consulta ===");

db.residuos.createIndex({ tipoResiduo: 1 }, { name: "uk_residuos_tipo", unique: true });
db.pontos_coleta.createIndex({ residuo: 1 }, { name: "idx_pontos_residuo" });
db.pontos_coleta.createIndex({ local: 1 }, { name: "idx_pontos_local" });
db.coletas.createIndex({ pontoColeta: 1, dataColeta: -1 }, { name: "idx_coletas_ponto_data" });
db.destinacoes.createIndex({ coleta: 1 }, { name: "idx_destinacoes_coleta" });
db.destinacoes.createIndex({ tipoDestino: 1 }, { name: "tipoDestino" });
db.alertas.createIndex({ dataAlerta: -1 }, { name: "idx_alertas_data" });

const objectIdFor = (collectionPrefix, position) => ObjectId(
    collectionPrefix + position.toString(16).padStart(2, "0")
);

const residuoId = (position) => objectIdFor("6600000000000000000000", position);
const pontoId = (position) => objectIdFor("6700000000000000000000", position);
const coletaId = (position) => objectIdFor("6800000000000000000000", position);
const destinacaoId = (position) => objectIdFor("6900000000000000000000", position);
const alertaId = (position) => objectIdFor("6a00000000000000000000", position);

print("=== CREATE: insertMany em residuos (11 documentos) ===");

printjson(db.residuos.insertMany([
    {
        _id: residuoId(1),
        tipoResiduo: "Plástico",
        limiteMaximo: NumberInt(1200),
        caracteristicas: {
            reciclavel: true,
            categoriasAceitas: ["PET", "PEAD", "PP"]
        }
    },
    {
        _id: residuoId(2),
        tipoResiduo: "Papel e papelão",
        limiteMaximo: NumberInt(900),
        caracteristicas: { reciclavel: true, exigeMaterialSeco: true }
    },
    {
        _id: residuoId(3),
        tipoResiduo: "Vidro",
        limiteMaximo: NumberInt(1500),
        caracteristicas: { reciclavel: true, coresAceitas: ["incolor", "verde", "âmbar"] }
    },
    {
        _id: residuoId(4),
        tipoResiduo: "Metal",
        limiteMaximo: NumberInt(1100),
        caracteristicas: { reciclavel: true, separacaoMagnetica: true }
    },
    {
        _id: residuoId(5),
        tipoResiduo: "Orgânico",
        limiteMaximo: NumberInt(700),
        caracteristicas: { compostavel: true, prazoColetaHoras: NumberInt(24) }
    },
    {
        _id: residuoId(6),
        tipoResiduo: "Eletrônico",
        limiteMaximo: NumberInt(500),
        caracteristicas: {
            reciclavel: true,
            requerDesmontagem: true,
            componentesCriticos: ["placas", "baterias"]
        }
    },
    {
        _id: residuoId(7),
        tipoResiduo: "Pilhas e baterias",
        limiteMaximo: NumberInt(250),
        caracteristicas: { perigoso: true, requerRecipienteLacrado: true }
    },
    {
        _id: residuoId(8),
        tipoResiduo: "Óleo de cozinha",
        limiteMaximo: NumberInt(400),
        unidadeAlternativa: "litros",
        caracteristicas: { reciclavel: true, requerBombona: true }
    },
    {
        _id: residuoId(9),
        tipoResiduo: "Têxtil",
        limiteMaximo: NumberInt(600),
        caracteristicas: { reutilizavel: true, aceitaRetalhos: true }
    },
    {
        _id: residuoId(10),
        tipoResiduo: "Madeira",
        limiteMaximo: NumberInt(1800),
        caracteristicas: { reciclavel: true, aceitaMadeiraTratada: false }
    },
    {
        _id: residuoId(11),
        tipoResiduo: "Material de demonstração",
        limiteMaximo: NumberInt(100),
        temporario: true
    }
]));

print("=== CREATE: insertMany em pontos_coleta (11 documentos) ===");

printjson(db.pontos_coleta.insertMany([
    {
        _id: pontoId(1),
        residuo: residuoId(1),
        local: "Parque Ibirapuera - Portão 3",
        capacidadeAtual: NumberInt(720),
        capacidadeMaxima: NumberInt(1000),
        endereco: { logradouro: "Av. Pedro Álvares Cabral", numero: "s/n", bairro: "Vila Mariana" },
        sensores: [{ tipo: "ultrassônico", status: "ATIVO", ultimaLeitura: ISODate("2026-09-05T12:00:00Z") }]
    },
    {
        _id: pontoId(2),
        residuo: residuoId(2),
        local: "Biblioteca Mário de Andrade",
        capacidadeAtual: NumberInt(310),
        capacidadeMaxima: NumberInt(800),
        endereco: { logradouro: "Rua da Consolação", numero: "94", cep: "01302-000" }
    },
    {
        _id: pontoId(3),
        residuo: residuoId(3),
        local: "Mercado Municipal de São Paulo",
        capacidadeAtual: NumberInt(900),
        capacidadeMaxima: NumberInt(1200),
        acessibilidade: { rotaAcessivel: true, pisoTatil: true }
    },
    {
        _id: pontoId(4),
        residuo: residuoId(4),
        local: "Estação Vila Olímpia",
        capacidadeAtual: NumberInt(450),
        capacidadeMaxima: NumberInt(900),
        funcionamento: { dias: ["seg", "ter", "qua", "qui", "sex"], inicio: "06:00", fim: "22:00" }
    },
    {
        _id: pontoId(5),
        residuo: residuoId(5),
        local: "Horta Comunitária do Butantã",
        capacidadeAtual: NumberInt(640),
        capacidadeMaxima: NumberInt(650),
        compostagemLocal: true
    },
    {
        _id: pontoId(6),
        residuo: residuoId(6),
        local: "Centro Cultural São Paulo",
        capacidadeAtual: NumberInt(280),
        capacidadeMaxima: NumberInt(450),
        responsavel: { organizacao: "Cooperativa Tech Verde", contato: "contato@techverde.example" }
    },
    {
        _id: pontoId(7),
        residuo: residuoId(7),
        local: "UBS Bela Vista",
        capacidadeAtual: NumberInt(190),
        capacidadeMaxima: NumberInt(200),
        seguranca: { recipienteLacrado: true, acessoRestrito: false }
    },
    {
        _id: pontoId(8),
        residuo: residuoId(8),
        local: "CEU Campo Limpo",
        capacidadeAtual: NumberInt(210),
        capacidadeMaxima: NumberInt(350),
        unidadeMedida: "litros"
    },
    {
        _id: pontoId(9),
        residuo: residuoId(9),
        local: "Subprefeitura da Lapa",
        capacidadeAtual: NumberInt(330),
        capacidadeMaxima: NumberInt(500),
        recebeDoacoes: true
    },
    {
        _id: pontoId(10),
        residuo: residuoId(10),
        local: "Ecoponto Tatuapé",
        capacidadeAtual: NumberInt(1100),
        capacidadeMaxima: NumberInt(1600),
        aceitaAgendamento: true
    },
    {
        _id: pontoId(11),
        residuo: residuoId(11),
        local: "Ponto temporário para demonstração",
        capacidadeAtual: NumberInt(10),
        capacidadeMaxima: NumberInt(100),
        temporario: true
    }
]));

print("=== CREATE: insertMany em coletas (11 documentos) ===");

printjson(db.coletas.insertMany([
    {
        _id: coletaId(1),
        pontoColeta: pontoId(1),
        dataColeta: ISODate("2026-08-25T09:00:00Z"),
        quantidadeColeta: NumberInt(420),
        veiculo: { prefixo: "ECO-101", tipo: "elétrico" },
        equipe: ["Ana Lima", "Carlos Souza"]
    },
    {
        _id: coletaId(2),
        pontoColeta: pontoId(2),
        dataColeta: ISODate("2026-08-26T10:30:00Z"),
        quantidadeColeta: NumberInt(280),
        veiculo: { prefixo: "ECO-102", tipo: "híbrido" }
    },
    {
        _id: coletaId(3),
        pontoColeta: pontoId(3),
        dataColeta: ISODate("2026-08-27T08:15:00Z"),
        quantidadeColeta: NumberInt(600),
        pesagem: { origemKg: NumberDecimal("602.40"), aferida: true }
    },
    {
        _id: coletaId(4),
        pontoColeta: pontoId(4),
        dataColeta: ISODate("2026-08-28T14:00:00Z"),
        quantidadeColeta: NumberInt(350),
        rota: { codigo: "R-04", distanciaKm: NumberDecimal("18.7") }
    },
    {
        _id: coletaId(5),
        pontoColeta: pontoId(5),
        dataColeta: ISODate("2026-08-29T07:45:00Z"),
        quantidadeColeta: NumberInt(500),
        processamentoLocal: "compostagem"
    },
    {
        _id: coletaId(6),
        pontoColeta: pontoId(6),
        dataColeta: ISODate("2026-08-30T11:20:00Z"),
        quantidadeColeta: NumberInt(190),
        cadeiaCustodia: [{ etapa: "lacrado", responsavel: "Marina Reis" }]
    },
    {
        _id: coletaId(7),
        pontoColeta: pontoId(7),
        dataColeta: ISODate("2026-08-31T16:10:00Z"),
        quantidadeColeta: NumberInt(170),
        protocoloSeguranca: "NBR-9191"
    },
    {
        _id: coletaId(8),
        pontoColeta: pontoId(8),
        dataColeta: ISODate("2026-09-01T13:00:00Z"),
        quantidadeColeta: NumberInt(160),
        unidadeMedida: "litros"
    },
    {
        _id: coletaId(9),
        pontoColeta: pontoId(9),
        dataColeta: ISODate("2026-09-02T09:40:00Z"),
        quantidadeColeta: NumberInt(240),
        triagem: { itensReutilizaveis: NumberInt(85), percentualAproveitamento: NumberDecimal("72.5") }
    },
    {
        _id: coletaId(10),
        pontoColeta: pontoId(10),
        dataColeta: ISODate("2026-09-03T15:30:00Z"),
        quantidadeColeta: NumberInt(950),
        veiculo: { prefixo: "ECO-110", tipo: "biodiesel" }
    },
    {
        _id: coletaId(11),
        pontoColeta: pontoId(11),
        dataColeta: ISODate("2026-09-04T12:00:00Z"),
        quantidadeColeta: NumberInt(10),
        temporario: true
    }
]));

print("=== CREATE: insertMany em destinacoes (11 documentos) ===");

printjson(db.destinacoes.insertMany([
    {
        _id: destinacaoId(1),
        coleta: coletaId(1),
        tipoDestino: "Reciclagem mecânica",
        unidadeReceptora: { nome: "Recicla SP", municipio: "São Paulo", licenca: "CETESB-1001" },
        percentualAproveitado: NumberDecimal("94.5")
    },
    {
        _id: destinacaoId(2),
        coleta: coletaId(2),
        tipoDestino: "Reciclagem de papel",
        unidadeReceptora: { nome: "Papel Circular", municipio: "Guarulhos" }
    },
    {
        _id: destinacaoId(3),
        coleta: coletaId(3),
        tipoDestino: "Beneficiamento de vidro",
        certificado: { numero: "CD-2026-003", emitidoEm: ISODate("2026-08-28T00:00:00Z") }
    },
    {
        _id: destinacaoId(4),
        coleta: coletaId(4),
        tipoDestino: "Siderurgia",
        percentualAproveitado: NumberDecimal("98.0")
    },
    {
        _id: destinacaoId(5),
        coleta: coletaId(5),
        tipoDestino: "Compostagem",
        produtoGerado: { tipo: "composto orgânico", quantidadeKg: NumberInt(210) }
    },
    {
        _id: destinacaoId(6),
        coleta: coletaId(6),
        tipoDestino: "Logística reversa",
        fabricanteResponsavel: "Consórcio Eletrônico Circular"
    },
    {
        _id: destinacaoId(7),
        coleta: coletaId(7),
        tipoDestino: "Tratamento especializado",
        manifestoTransporte: "MTR-2026-7007"
    },
    {
        _id: destinacaoId(8),
        coleta: coletaId(8),
        tipoDestino: "Produção de biodiesel",
        conversaoEstimadaLitros: NumberDecimal("142.5")
    },
    {
        _id: destinacaoId(9),
        coleta: coletaId(9),
        tipoDestino: "Reuso social",
        beneficiarios: { organizacoes: NumberInt(3), pessoasEstimadas: NumberInt(120) }
    },
    {
        _id: destinacaoId(10),
        coleta: coletaId(10),
        tipoDestino: "Reaproveitamento energético",
        unidadeReceptora: { nome: "BioMadeira", municipio: "Mauá" }
    },
    {
        _id: destinacaoId(11),
        coleta: coletaId(11),
        tipoDestino: "Destino temporário",
        temporario: true
    }
]));

print("=== CREATE: insertMany em alertas (11 documentos) ===");

printjson(db.alertas.insertMany([
    {
        _id: alertaId(1),
        mensagem: "Ponto do Parque Ibirapuera atingiu 72% da capacidade.",
        dataAlerta: ISODate("2026-09-01T09:00:00Z"),
        severidade: "INFORMATIVO",
        origem: { tipo: "SENSOR", pontoColetaId: pontoId(1), leituraPercentual: NumberInt(72) }
    },
    {
        _id: alertaId(2),
        mensagem: "Coleta de papel deve ser programada para os próximos dois dias.",
        dataAlerta: ISODate("2026-09-01T10:15:00Z"),
        severidade: "ATENCAO",
        origem: { tipo: "REGRA_CAPACIDADE", pontoColetaId: pontoId(2) }
    },
    {
        _id: alertaId(3),
        mensagem: "Sensor do recipiente de vidro voltou a transmitir normalmente.",
        dataAlerta: ISODate("2026-09-01T11:30:00Z"),
        severidade: "RESOLVIDO",
        historico: [{ status: "ABERTO" }, { status: "RESOLVIDO", em: ISODate("2026-09-01T11:30:00Z") }]
    },
    {
        _id: alertaId(4),
        mensagem: "Rota de coleta de metais apresenta atraso estimado de 25 minutos.",
        dataAlerta: ISODate("2026-09-02T08:20:00Z"),
        severidade: "ATENCAO",
        origem: { tipo: "OPERACAO", rota: "R-04" }
    },
    {
        _id: alertaId(5),
        mensagem: "Ponto de resíduos orgânicos atingiu nível crítico de capacidade.",
        dataAlerta: ISODate("2026-09-02T12:40:00Z"),
        severidade: "CRITICO",
        origem: { tipo: "SENSOR", pontoColetaId: pontoId(5), leituraPercentual: NumberInt(98) },
        requerAcaoAte: ISODate("2026-09-02T18:00:00Z")
    },
    {
        _id: alertaId(6),
        mensagem: "Documento de cadeia de custódia de eletrônicos foi anexado.",
        dataAlerta: ISODate("2026-09-03T09:10:00Z"),
        severidade: "INFORMATIVO",
        origem: { tipo: "GOVERNANCA", coletaId: coletaId(6) }
    },
    {
        _id: alertaId(7),
        mensagem: "Recipiente de pilhas e baterias deve ser recolhido com prioridade.",
        dataAlerta: ISODate("2026-09-03T14:25:00Z"),
        severidade: "CRITICO",
        procedimentos: ["isolar recipiente", "acionar equipe certificada"]
    },
    {
        _id: alertaId(8),
        mensagem: "Bombona de óleo de cozinha passou pela inspeção de vedação.",
        dataAlerta: ISODate("2026-09-04T08:00:00Z"),
        severidade: "RESOLVIDO",
        inspecao: { responsavel: "João Mendes", conforme: true }
    },
    {
        _id: alertaId(9),
        mensagem: "Campanha de doação têxtil alcançou a meta semanal.",
        dataAlerta: ISODate("2026-09-04T16:45:00Z"),
        severidade: "INFORMATIVO",
        indicadorSocial: { metaKg: NumberInt(200), realizadoKg: NumberInt(240) }
    },
    {
        _id: alertaId(10),
        mensagem: "Licença da unidade receptora de madeira foi validada.",
        dataAlerta: ISODate("2026-09-05T10:00:00Z"),
        severidade: "INFORMATIVO",
        origem: { tipo: "COMPLIANCE", licenca: "CETESB-9010" }
    },
    {
        _id: alertaId(11),
        mensagem: "Alerta temporário utilizado para demonstrar a exclusão.",
        dataAlerta: ISODate("2026-09-05T11:00:00Z"),
        severidade: "TESTE",
        temporario: true
    }
]));

print("=== READ: consultas com find ===");

print("residuos recicláveis com limite a partir de 1000 kg:");
db.residuos.find({
    "caracteristicas.reciclavel": true,
    limiteMaximo: { $gte: NumberInt(1000) }
}).sort({ tipoResiduo: 1 }).forEach(printjson);

print("pontos de coleta com ocupação próxima do limite:");
db.pontos_coleta.find({
    $expr: { $gte: ["$capacidadeAtual", { $multiply: ["$capacidadeMaxima", 0.9] }] }
}).forEach(printjson);

print("coletas realizadas a partir de setembro de 2026:");
db.coletas.find({ dataColeta: { $gte: ISODate("2026-09-01T00:00:00Z") } })
    .sort({ dataColeta: 1 })
    .forEach(printjson);

print("destinações relacionadas à economia circular:");
db.destinacoes.find({
    tipoDestino: { $in: ["Reciclagem mecânica", "Logística reversa", "Reuso social"] }
}).forEach(printjson);

print("alertas críticos:");
db.alertas.find({ severidade: "CRITICO" }).sort({ dataAlerta: -1 }).forEach(printjson);

print("=== UPDATE: updateOne e leitura do documento alterado ===");

printjson(db.residuos.updateOne(
    { _id: residuoId(1) },
    {
        $set: { limiteMaximo: NumberInt(1300), ultimaRevisao: ISODate("2026-09-05T12:30:00Z") }
    }
));
printjson(db.residuos.findOne({ _id: residuoId(1) }));

printjson(db.pontos_coleta.updateOne(
    { _id: pontoId(5) },
    {
        $set: { capacidadeAtual: NumberInt(300), ultimaColetaEm: ISODate("2026-09-05T13:00:00Z") }
    }
));
printjson(db.pontos_coleta.findOne({ _id: pontoId(5) }));

printjson(db.coletas.updateOne(
    { _id: coletaId(4) },
    { $set: { quantidadeColeta: NumberInt(365), observacao: "Peso confirmado na unidade receptora" } }
));
printjson(db.coletas.findOne({ _id: coletaId(4) }));

printjson(db.destinacoes.updateOne(
    { _id: destinacaoId(10) },
    { $set: { tipoDestino: "Reaproveitamento de biomassa", statusComprovante: "VALIDADO" } }
));
printjson(db.destinacoes.findOne({ _id: destinacaoId(10) }));

printjson(db.alertas.updateOne(
    { _id: alertaId(7) },
    { $set: { severidade: "RESOLVIDO", resolvidoEm: ISODate("2026-09-05T14:00:00Z") } }
));
printjson(db.alertas.findOne({ _id: alertaId(7) }));

print("=== DELETE: deleteOne dos documentos temporários ===");

// A exclusão ocorre na ordem inversa das referências entre os documentos.
printjson(db.alertas.deleteOne({ _id: alertaId(11), temporario: true }));
printjson(db.destinacoes.deleteOne({ _id: destinacaoId(11), temporario: true }));
printjson(db.coletas.deleteOne({ _id: coletaId(11), temporario: true }));
printjson(db.pontos_coleta.deleteOne({ _id: pontoId(11), temporario: true }));
printjson(db.residuos.deleteOne({ _id: residuoId(11), temporario: true }));

print("=== RESULTADO FINAL: 10 documentos por collection ===");

printjson({
    residuos: db.residuos.countDocuments(),
    pontos_coleta: db.pontos_coleta.countDocuments(),
    coletas: db.coletas.countDocuments(),
    destinacoes: db.destinacoes.countDocuments(),
    alertas: db.alertas.countDocuments()
});

print("Inicialização e demonstração de CRUD concluídas com sucesso.");
