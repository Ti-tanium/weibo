const config = require("../config");
module.exports = require("knex")({
  client: "mysql",
  connection: {
    host: config.mysql.host,
    user: config.mysql.user,
    password: config.mysql.pass,
    database: config.mysql.db
  }
});
