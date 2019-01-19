const router = require("koa-router")();
const mysql = require("../databse/knex");
router.prefix("/users");

router.get("/auth", async (ctx, next) => {
  console.log("request:", ctx.query);
  const account = ctx.query.account;
  const sql = await mysql("register_user")
    .select("USER_ID")
    .where("USER_ID", account);
  console.log(sql);
  if (sql.length == 0) {
    ctx.body = {
      code: -1,
      msg: "No such user,please register first."
    };
  } else {
    ctx.body = {
      code: 1,
      msg: "User exists."
    };
  }
});

router.get("/pass", async (ctx, next) => {
  const account = ctx.query.account;
  const password = ctx.query.password;
  const sql = await mysql("user_password").where({
    USER_ID: account,
    PASSWORD: password
  });
  console.log("sql:", sql);
  if (sql.length == 0) {
    ctx.body = {
      code: -1,
      msg: "Wrong Password."
    };
    console.log("Wrong password,access dennied.");
  } else {
    ctx.body = {
      code: 1,
      msg: "Authentificated."
    };
    console.log("right password,access permitted.");
  }
});

module.exports = router;
