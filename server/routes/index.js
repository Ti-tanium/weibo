const router = require("koa-router")();
const mysql = require("../databse/knex");
const config = require("../config");
const fs = require("fs");

let storeImage = function(path, image) {
  path = "public/resources/" + Date.now() + ".png"; //从app.js级开始找--在我的项目工程里是这样的
  var dataBuffer = new Buffer(image, "base64"); //把base64码转成buffer对象，
  console.log("dataBuffer是否是Buffer对象：" + Buffer.isBuffer(dataBuffer));
  fs.writeFile(path, dataBuffer, function(err) {
    //用fs写入文件
    if (err) {
      console.log(err);
    } else {
      console.log("写入成功！");
    }
  });
};

router.get("/post", async (ctx, next) => {
  ctx.request;
  const title = ctx.query.title;
  const detail = ctx.query.detail;
  const userId = ctx.query.userid;
  const posttime = ctx.query.posttime;
  const WEIBOID = ctx.query.weiboid;
  const COMMENTS = "";
  console.log({ title, detail, userId, posttime, WEIBOID });
  const IMAGE = ctx.query.image;
  const FORWORD_COUNT = 0;
  const THUMBUP_COUNT = 0;

  try {
    await mysql("weibo").insert({
      TITLE: title,
      DETAIL: detail,
      WEIBOID: WEIBOID,
      CREATED_TIME: posttime,
      THUMBUP_COUNT: THUMBUP_COUNT,
      FORWARD_COUNT: FORWORD_COUNT,
      IMAGE: IMAGE,
      COMMENTS: COMMENTS,
      POSTID: userId
    });
    ctx.body = {
      code: 1,
      msg: "Succeed in posting."
    };
  } catch (e) {
    ctx.body = {
      code: -1,
      msg: "Failed to insert weibo information into database:" + e.sqlMessage
    };
    console.log(e.sqlMessage);
  }
});

router.get("/getweibolist", async (ctx, next) => {
  const sql = await mysql("weibo").select("weibo.*");
  console.log(sql);

  ctx.body = { sql };
});

router.get("/comment", async (ctx, next) => {
  console.log("/comment", ctx.query);
  const comment = ctx.query.comment;
  const comment_time = ctx.query.comment_time;
  const commenterId = ctx.query.commenterId;
  const commentId = ctx.query.commentId;
  const weibo_id = ctx.query.weiboId;

  try {
    await mysql("comment").insert({
      COMMENT_ID: commentId,
      CONTENT: comment,
      COMMENT_TIME: comment_time,
      COMMENTER: commenterId,
      WEIBO_ID: weibo_id
    });
    console.log("comment insert done.");
    const old = await mysql("weibo")
      .select("COMMENTS")
      .where("WEIBOID", weibo_id);
    console.log(old);
    var oldComments = old[0].COMMENTS;
    console.log("old comments got:", oldComments);
    await mysql("weibo")
      .where("WEIBOID", weibo_id)
      .update({
        COMMENTS: oldComments === "" ? commentId : oldComments + "`" + commentId
      });
    console.log("update comments done");
    ctx.body = {
      code: 1,
      msg: "succeed in commenting"
    };
  } catch (e) {
    ctx.body = {
      code: -1,
      msg: "Failed to insert comment into mysql:" + e.sqlMessage
    };
    console.log(e.sqlMessage);
  }
});

router.get("/getcomment", async (ctx, never) => {
  const query = ctx.query;
  console.log("/getcomment", query);
  var comments = [];
  const commentsId = query.comment.split(",");
  console.log("comment id array:", commentsId);
  for (c in commentsId) {
    console.log("comment id:" + commentsId[c]);
    var content;
    if (commentsId[c] != "") {
      content = await mysql("comment")
        .select("*")
        .where("COMMENT_ID", commentsId[c]);
    }
    if (content) {
      comments.push(content[0]);
    }
  }
  ctx.body = {
    comments: comments
  };
});

module.exports = router;
