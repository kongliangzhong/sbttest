namespace java com.yunrang.social.comitium.acl
namespace cpp social.comitium.acl
namespace py social_comitium_acl
namespace php Social_Comitium_acl

enum PolicyPredicate {
  NO_ONE         = 0
  ONLY_ME        = 1
  ONLY_FOLLOWERS = 2
  ONLY_FOLLOWEES = 3
  EVERY_ONE      = 4
  INCLUDING      = 5
  EXCLUDING      = 6
}

enum PolicyType {
  SEE_FORWARD_QUESTION_UPDATE = 0
  SEE_QUESTION                = 1
}

struct Policy {
  1:optional PolicyPredicate predicate
  2:optional list<i64> userIds
}

struct ACL {
  1:map<PolicyType, Policy> policyMap
}
