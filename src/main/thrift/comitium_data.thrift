////////////////////////////////////////////////////////////////////////////////////////////////////
//  This file defines data structures used by Comitium service.                                   //
//  The data definitions reflect one-to-one, many-to-one, but not one-to-many and many-to-many    //
//  relations.                                                                                    //
//  Note: Topics, Reviews, Questions, Answers, Comments, and Status Updates share the same i64    //
//        id space, while Users, Photos, each has their own i64 id space.                         //
//  Author: Dong Wang (dongw@yunrang.com)                                                         //
////////////////////////////////////////////////////////////////////////////////////////////////////
namespace java com.yunrang.social.comitium
namespace cpp social.comitium
namespace py social_comitium
namespace php Social_Comitium

include "social/proto/common.thrift"
include "social/proto/comitium_acl.thrift"

const string COMITIUM_OBJECT_SUDO_URL_PREFIX = "http://www.yunrang.com/comitium/data/"

typedef string Cursor
typedef common.Image  Image
typedef comitium_acl.ACL ACL

////////// COMMON /////////////////////////////////////////
enum State {
  NORMAL  = 0
  DELETED = 1
}

enum AuditStatus {
  NOT_APPLICABLE = 0
  PENDING        = 1
  DENIED         = 2
  APPROVED       = 3
}

enum ImpressionType {
  CHANGE = 0
  VIEW = 1
}

struct BasicInfo {
  1:i64 id = 0
  2:optional string url
  3:optional /* transient */ i64 updated = 0 // seconds since epoch
  4:optional State state = State.NORMAL
  5:optional list<string> topics
  6:optional i64 last_accessed_timestamp = 0
  7:optional list<Image> images
  8:optional ACL acl
  9:optional common.Rank rank
  10:optional AuditStatus audit_status = AuditStatus.NOT_APPLICABLE
  11:optional i64 created = 0
  12:optional list<i64> topic_ids
}

struct SearchMetadata {
  1:string author_name
  4:optional i32 social_score
  5:optional list<string> ref_urls
  // url for doc may be not clickable(sometimes is faked, eg. weibo of sina),
  // so a clickable_url is needed
  6:optional string clickable_url
  7:optional string linked_content_clickable_url
}

struct SharingURL {
  1:string url
  2:optional string title
  3:optional string content
  4:optional i64 icon
  5:optional i64 image
  6:optional i64 timestamp
  7:optional string snapshot
}

////////// USER ///////////////////////////////////////////
struct SocialMetadata {
  1: optional bool i_follow= 0,
  2: optional bool follow_me = 0,
  3: optional bool i_like = 0,
  4: optional bool not_helpful = 0,
}

// WARNING: whenever you change this struct, please make sure Dong Wang is aware of the change.
// Otherwise Comitium will NOT correctly save this information.
struct UserStats {
  1: optional i32 num_followers = 0
  2: optional i32 num_following = 0
  3: optional i32 num_following_topics = 0
  5: optional i32 num_new_updates = 0          // deprecated, no longer supported
  6: optional i32 num_topics_owned = 0
  7: optional i32 num_questions_I_asked = 0
  8: optional i32 num_reviews = 0
  9: optional i32 num_answers = 0
  10:optional i32 num_following_questions = 0
  11:optional i32 num_new_conversations = 0    // deprecated, no longer supported
  12:optional i32 num_suggested_users = 0      // deprecated, no longer supported
  13:optional i32 num_suggested_topics = 0     // deprecated, no longer supported
  14:optional i32 num_suggested_questions = 0  // deprecated, no longer supported
  15:optional i32 num_questions_asked_me = 0
  16:optional i32 num_answers_liked = 0  //num of reviews/answers being liked
  17:optional i32 num_reviews_liked = 0
  18:optional i32 num_liking_reviews = 0 //num of reviews/answers I liked
  19:optional i32 num_liking_answers = 0
  20:optional i32 num_likes_of_reviews = 0  //num of likes of use's reviews or answers
  21:optional i32 num_likes_of_answers = 0
}

struct UserInfo {
  1: i64 user_id
  2: optional SocialMetadata social_meta
  3: optional UserStats stats
}

enum ExternalUserType {
  MAIL = 1
  MSNIM = 200

  WEIBO = 300
  RENREN = 301
  T_QQ = 302
  KAINXIN001 = 303
  DOUBAN = 304
}

struct ExternalUser {
  1:optional i64 user_id
  2:optional string user_url
  3:optional ExternalUserType user_type
}

struct IdAndWeight {
  1: i64 id
  2:optional i32 weight = 0
}

////////// COMMENT ////////////////////////////////////////
struct Comment {
  1:i64 id= 0
  2:i64 author_id = 0
  3:string content
  4:optional i64 created = 0
}

////////// REVIEW /////////////////////////////////////////
struct ReviewStats {
  1:optional i32 num_comments = 0
  2:optional i32 num_likes = 0
  3:optional i32 num_forwards = 0
}

struct OriginalDocumentId {
  1: optional i64 topic_id = 0
  2: optional i64 review_id = 0
  3: optional i64 answer_id = 0
  4: optional i64 question_id = 0
}

struct Review {
  1:BasicInfo base
  2:i64 author_id = 0
  3:i64 topic_id = 0
  4:string content
  5:optional i32 score = -1   // [0-100] -1 means no data available

  6:/* transient */ optional SocialMetadata social_meta
  7:/* transient */ optional ReviewStats stats
  8:/* transient */ optional SearchMetadata search_meta
  9:/* transient */ optional list<Comment> comments
  10: optional string title
  11: optional string url
  12: optional OriginalDocumentId original_id //this field is used to refer to the document one re-tweets.
  13: optional SharingURL sharing_url
}

////////// ANSWER /////////////////////////////////////////
struct AnswerStats {
  1:optional i32 num_likes = 0
  2:optional i32 num_comments = 0
  3:optional i32 num_not_helpful = 0
  4:optional i32 num_forwards = 0
}

struct Answer {
  1:BasicInfo base
  2:i64 author_id = 0
  3:i64 question_id = 0
  4:string content
  5:optional bool is_thanked = 0 // deprecated useless

  6:/* transient */ optional SocialMetadata social_meta
  7:/* transient */ optional AnswerStats stats
  8:/* transient */ optional SearchMetadata search_meta
  9:/* transient */ optional list<Comment> comments
  10: optional SharingURL sharing_url
}

////////// QUESTION ///////////////////////////////////////
struct QuestionStats {
  1:optional i32 num_followers = 0
  2:optional i32 num_answers = 0
  3:optional i32 num_comments = 0
  4:optional i32 num_related_questions = 0
  5:optional i32 num_forwards = 0
}

struct Question {
  1:BasicInfo base
  2:i64 author_id = 0
  3:string title
  4:optional string description
  5:optional bool routed = 0          // Whether the question have been routed to selected users.
  6:optional i64 main_question_id = 0 // non-0, if this question is a follow-up question.
  7:optional i64 redirect_to = 0      // if this question is essentially the same as another one.
  8:optional i32 reliability = -1    // reliability score of the the question.
                                      // [0 - 100], -1 means no data available.
  9:optional bool anonymous = 0      // if True, this question's author info will be hidden, and
                                      // the author's followers won't get any update regarding
                                      // this question.

  10:/* transient */ optional SocialMetadata social_meta
  11:/* transient */ optional QuestionStats stats,
  12:/* transient */ optional SearchMetadata search_meta
  13:/* transient */ optional list<Answer> answers
  14:/* transient */ optional list<Comment> comments
  15:optional i64 topic_id = 0        // if the question is asked in a topic's page, set this topic_id.
  16:/* transient */ optional i64 forwarded_by = 0
  17:optional bool is_external_question = 0
  18: optional SharingURL sharing_url
}

////////// TOPIC //////////////////////////////////////////
enum ValueType {
  STRING = 0
  INTEGER = 1
  DOUBLE = 2
  DATETIME = 3
  URL = 4
  RICHTEXT = 5
}

struct Annotation {
  1:string name
  2:ValueType type  // Deprecated
  3:optional list<string> str_value
  4:optional i64 int_value
  5:optional double double_value
  6:optional list<string> seg_str_value
  7:optional bool is_key_anno = 0
  8:optional i64 id = 0;
  9:optional string seg_name;
}

struct AnnotationGroup {
  1:i32 group_id
  2:list<Annotation> items
}

struct TopicStats {
  1:optional i32 num_followers = 0
  2:optional i32 num_reviews = 0
  3:optional i32 num_questions = 0
  4:optional i32 num_related_topics = 0
  5:optional i32 num_forwards = 0
}

enum TopicProgress {
  KEYWORD = 1,
  TEMPLATE = 2,
  TOPIC = 3,
}

struct Topic {
  // NOTE: the base.id == FP(title) is always true for topics.
  1:BasicInfo base
  2:string title
  3:optional string description  // DEPRECATED
  4:optional list<Annotation> annotations
  5:optional Image icon_image
  6:optional i32 document_schema_id = 0

  8:/* transient */ optional SocialMetadata social_meta
  9:/* transient */ optional TopicStats stats
  10:/* transient */ optional SearchMetadata search_meta
  11:/* transient */ optional list<Review> reviews
  12:/* transient */ optional list<Question> questions
  13:optional i64 author_id = 0
  14:optional i64 mysql_id = 0
  15:optional TopicProgress progress = TopicProgress.TOPIC
  16:optional list<AnnotationGroup> annotation_groups
}

////////// ACTVITY //////////////////////////////////////////

enum Action {
  //Next value to use: 67
  UNKNOWN               = 0

  FOLLOW_USER           = 1
  UNFOLLOW_USER         = 2
  FOLLOW_TOPIC          = 3
  UNFOLLOW_TOPIC        = 4
  FOLLOW_QUESTION       = 5
  UNFOLLOW_QUESTION     = 6
  LIKE_REVIEW           = 7
  UNLIKE_REVIEW         = 8
  //LIKE_COMMENT        = 9   // deprecated
  //UNLIKE_COMMENT      = 10  // deprecated
  LIKE_ANSWER           = 11
  UNLIKE_ANSWER         = 12

  POST_REVIEW           = 21
  DELETE_REVIEW         = 22
  UPDATE_REVIEW         = 23
  RESTORE_REVIEW        = 24

  POST_QUESTION         = 25
  DELETE_QUESTION       = 26
  UPDATE_QUESTION       = 27
  RESTORE_QUESTION      = 28

  POST_ANSWER           = 29
  DELETE_ANSWER         = 30
  UPDATE_ANSWER         = 31
  RESTORE_ANSWER        = 32

  COMMENT_ANSWER        = 33
  DELETE_ANSWER_COMMENT = 34
  UPDATE_ANSWER_COMMENT = 35

  FORWARD_QUESTION      = 36
  THANK_ANSWER          = 37 // deprecated
  UNTHANK_ANSWER        = 38 // deprecated

  DELETE_TOPIC          = 39
  UPDATE_TOPIC          = 40
  RESTORE_TOPIC         = 41
  POST_TOPIC            = 42

  COMMENT_QUESTION        = 43
  DELETE_QUESTION_COMMENT = 44
  UPDATE_QUESTION_COMMENT = 45

  COMMENT_REVIEW          = 46
  DELETE_REVIEW_COMMENT   = 47
  UPDATE_REVIEW_COMMENT   = 48

  MARK_ANSWER_NOT_HELPFUL   = 49
  UNMARK_ANSWER_NOT_HELPFUL = 50

  UPDATE_AUDIT_STATUS   = 51

  VISIT_USER            = 52 //deprecated
  VISIT_TOPIC           = 53 //deprecated
  VISIT_QUESTION        = 54 //deprecated

  ADD_TOPIC_IMAGES      = 55
  DELETE_TOPIC_IMAGES   = 58
  INVITE_FOLLOW_TOPIC   = 56

  LIKE_URL              = 57

  COMMENT_IMAGE         = 59
  DELETE_IMAGE_COMMENT  = 60

  ADD_GADGET               = 61
  UPDATE_GADGET            = 62
  ADD_GADGETDEPLOYMENT     = 63
  UPDATE_GADGETDEPLOYMENT  = 64

  DELETE_QUESTION_ANSWERS = 65

  FORWARD_URL           = 66

  CLICK_USER_PROFILE = 1000
  CLICK_TOPIC        = 1001
  CLICK_QUESTION     = 1002
  CLICK_REVIEW       = 1003

  MENTION_USER_FROM_QUESTION  = 1004  // mention a user
  MENTION_TOPIC_FROM_QUESTION = 1005  // mention a topic
  MENTION_USER_FROM_ANSWER    = 1006
  MENTION_TOPIC_FROM_ANSWER   = 1007
  MENTION_USER_FROM_REVIEW    = 1008
  MENTION_TOPIC_FROM_REVIEW   = 1009
  MENTION_USER_FROM_TOPIC     = 1010
  MENTION_TOPIC_FROM_TOPIC    = 1011
}

struct IdWithText {
   1:i64 id
   2:optional string text
   3:optional list<string> keywords
   4:optional i64 author_id
   5:optional AuditStatus original_audit_status
   6:optional AuditStatus new_audit_status
   7:optional list<i64> images
   8:optional list<i64> topic_ids
   9:optional i64 update_timestamp
}

struct Activity {
  1:i64 id
  2:i64 user_id
  3:Action action
  4:optional IdWithText topic         // text is the topic's title
  5:optional IdWithText review
  6:optional IdWithText question      // text is the question's title
  7:optional IdWithText answer
  8:optional IdWithText comment
  9:optional IdWithText target_user   // deprecated
  10:optional ACL acl
  15:optional i64 timestamp = 0
  16:optional list<i64> target_multiple_users
  17:optional i32 user_group_type = 1 // 1:USER 2:EDITOR 3:ADMIN (then same value in user.GroupType)
  18:optional string url
  19:optional i64 image = 0 // is field is used to comment image
  20:optional IdWithText gadget
  21:optional IdWithText gadget_deployment
  22:optional OriginalDocumentId original_id
}

struct TopicProportion {
  1:required i64 topic_id = 0;
  2:required double proportion;
  3:optional string topic_tag;
}


////////// NOTIFICATION /////////////////////////////////////////
enum NotificationType {
  DIRECT    = 0
  MENTIONED = 1
  OWNED_DOC_CHANGED = 2
  LINKED_DOC_CHANGED= 3
}

struct Notification {
  1:i64 id
  2:Activity activity
  3:optional NotificationType notificationType = 0
}

////////// DIRECT MESSAGE /////////////////////////////////////////
struct DirectMessage {
  1:i64 id
  2:i64 sender_id
  3:i64 recipient_id
  4:string content
  5:optional bool is_read = 1 //deprecated
  6:optional i64 timestamp = 0
}

////////// CONVERSATION SUMMARY /////////////////////////////////////////
struct ConversationSummary {
  1:i64 user_id
  2:bool is_read
  3:DirectMessage message
}

////////// EDGE ATTRIBUTE /////////////////////////////////////////
struct EdgeAttribute {
  1:optional double weight = 0.0
  2:optional bool edited = 0
  3:optional bool deleted = 0
  4:optional list<i64> reason
  5:optional i64 id = 0
}

///////// GROUPED /////////////////////////////////////////////////
struct TopicSegment {1: list<Topic> topics, 2: optional Cursor nextCursor}
struct ReviewSegment {1: list<Review> reviews, 2: optional Cursor nextCursor}
struct QuestionSegment {1: list<Question> questions, 2: optional Cursor nextCursor}
struct AnswerSegment {1: list<Answer> answers, 2: optional Cursor nextCursor}
struct ActivitySegment {1: list<Activity> activities, 2: optional Cursor nextCursor}
struct NotificationSegment {1: list<Notification> notifications, 2: optional Cursor nextCursor}
struct NamedCursorSegment {1: map<string, string> nameWithValue, 2: optional Cursor nextCursor}

///////// EDGEINFO /////////////////////////////////////////////////
struct EdgeRank {
    1:i64 targetId
    2:i32 targetType
    3:double weight = 0.0
}

struct EdgeRankSegment {
    1:list<EdgeRank> edgeRanks
}
// will be deprecated
struct EdgeInfo {
    1:i64 sourceId
    2:i64 destinationId
    3:i64 timestamp
    4:optional double weight = 0.0
    5:optional i64 checkPoint = 0
}
////////// URL ////////////////////////////////////////////
struct UrlStats {
  1:optional i32 num_likes = 0
  2:optional i32 num_forwards = 0
}

struct UrlActionParam {
}

struct Url {
  1:string url
  2:optional UrlStats url_stats
  3:optional SocialMetadata social_meta
}

//////////////////// Contest Event Info //////////////////////////
// Basic information about a contest event                      //
// 1 - Round = 0 用来记录总分，和总排名                             //
// 2 - 用户第一次加入活动时，调用saveUserEventInfo，来创建一个用户总分的 //
//     记录（包括加入时间）。以后get joined_timestamp时，直接 get     // 
//     Round 0的UserEventInfo即可。                              // 
//////////////////// Contest Event Info //////////////////////////
enum Event {
  UNKNOWN = 0
  MAX_LIKES_OF_USER = 1 // people who won the most likes
}

struct EventInfo {
  1:Event event
  2:optional i32 current_round
  3:optional string current_random_users
}

struct EventScore {
  1:i32 score
  2:optional i32 position                       //在所有参与者中的排名
  3:optional i32 college_position               //在用户所属学校中的排名
  4:optional i32 last_score                     //上一次的分数 never used
  5:optional i32 last_position                  //上一次的排名
}

// Query/Index by event id + user id + round num
//  - logs users' score and position information
// Query/Index by event id and user id
// - Indicate the participation between a user and an contest event
// - Logs history scores for each round
struct UserEventInfo {
  1:i64 user_id
  2:Event event
  3:optional i32 round
  4:optional i64 joined_timestamp              // in UTC timestamp, when joined this event
  5:optional EventScore score
}
