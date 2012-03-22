///////////////////////////////////////////////////////////////////////////////////////////////////
//  This file defines common structures that are not domain specific.                            //
//  Warning: These data types are NOT to be used for persistence.                                //
//  Author: Dong Wang (dongw@yunrang.com)                                                        //
///////////////////////////////////////////////////////////////////////////////////////////////////
namespace java com.yunrang.social.common
namespace cpp social.common
namespace py social_common
namespace php Social_Common

typedef i64 ID
typedef i64 Timestamp

// These result codes are used as return values or as part of exceptions for both Authsess an
// Usermgmt. These result code's value may change so they should not be used for any persistence.
enum TCommonResultCode {
  OK                         = 0,  // No error, shouldn't be used by exceptions.
  // 用户相关错误代码
  USER_EXIST                 = 1,   // 用户已经存在
  USER_UNVALIDATED           = 12,  // 用户未经邮件验证
  USER_VALIDATED             = 15,  // 用户已经过邮件验证
  USER_DELETED               = 16,  // 用户已经被删除
  USER_NOT_EXIST             = 2,   // 该用户不存在
  USER_URI_INVALID           = 3,   // user_uri非法
  PASSWORD_INVALID           = 4,   // 非法密码
  PASSWORD_MISMATCH          = 5,   // 密码错误
  VCODE_INVALID              = 13,  // 验证码不正确
  VCODE_EXPIRED              = 14,  // 验证码已过期
  // others
  SERVICE_REGISTERED         = 6,
  SERVICE_NOT_REGISTERED     = 7,
  SERVICE_MISCONFIGGED       = 8,
  INVALID_SESSION            = 9,
  UNKNOWN_UPSTREAM_ERROR     = 10,
  UNKNOWN_ERROR              = 11,
  OAUTH_KEY_EXPIRED          = 17,
  EMPTY_SET                  = 18,  // 查询结果为空集
  EXTERNAL_USER_BINDED       = 19,  //the external user has been binded to yy uri
  NOT_EXTERNAL_USER          = 20,
  NOT_INTERNAL_USER          = 21,
  NOT_ADMIN                  = 22,
  VCODE_REGISTERED           = 23,  // 验证码/邀请码已经注册
  INVITE_LIMITED             = 24, //邀请数量受到限制，达到最大限制
  EMAIL_EXIST                = 25,
}

exception TCommonException {
  1:TCommonResultCode statusCode = TCommonResultCode.OK,
  2:optional string reason = "",
  3:optional string server = "",
}

// Cursor value null (and "") indicates the top or the end of the data.
// Cursors are not comparable, therefore you cannot use cursor1 > cursor2 or as such.
struct Page {
  1: i32 count
  2: string cursor
  3: optional bool reverse = 1  // If true (default), paging from newest to oldest
}

enum ImageSize {
  SMALL = 1,
  MIDDLE = 2,
  BIG = 3,
}

struct Image {
  1:optional i64 id      // For internal photos, mutual exclusive with url. If both exist, id will be used.
  2:/* transient */ optional string key  // For internal photos, will not be persisted to DB.
                                          // The key will be generated right before such an Image object is returned to client.
  3:optional string url  // external photos
  4:optional ImageSize image_size = ImageSize.SMALL  // image size
}

///////////////////////////////////////////////////////////////////////////////
//  WARNING:                                                                 //
//    if you change this Rank structure, please make sure Dong Wang knows    //
//    about it. Otherwise comitium will NOT be able to save this information //
//    into database correctly.                                               //
///////////////////////////////////////////////////////////////////////////////

struct Rank {
  // 用户排名，用户本身或者内容创建者、参与者的综合评分
  1:optional i32 user_rank
  // 内容本身质量的评分
  2:optional i32 content_rank
  // 根据内容相关网址计算出来的评分，例如Topic的官方网址，来源网址的Pagerank
  3:optional i32 ref_rank
  // 反映内容在系统中的受欢迎程度，例如Topic的关注人数，Review的Like数与评论数，用户的粉丝数
  4:optional i32 hot_rank
  // 根据用户贡献计算出的评分，包括用户发表Topic, Review, Answer, Question的数目
  5:optional i32 contrib_rank

  // hot ranks within certain time period
  6:optional i32 last_day_hot_rank
  7:optional i32 last_week_hot_rank
  8:optional i32 last_month_hot_rank
  9:optional i32 all_time_hot_rank

  10:optional i32 spam_score;

  100:optional string debug_info
}

struct RequestHeader {
  // TBD
}
