namespace java cn.kongliangzhong.thrift

struct TestResult {
  1:string name
  2:i64 timestamp
}


service TestService {
  void printNameAndTs(1:string name)
  TestResult getResult()
}