# 好友关系和好友申请API文档

## 好友关系接口 (FriendsController)

### 1. 同意好友申请
- **URL**: `/friends/agree`
- **方法**: POST
- **描述**: 同意好友申请
- **请求参数**:
  ```json
  {
    "id": 123 // 好友申请ID
  }
  ```
- **响应**:
  ```json
  {
    "code": 0,
    "data": true,
    "message": "ok"
  }
  ```

### 2. 拒绝好友申请
- **URL**: `/friends/disagree`
- **方法**: POST
- **描述**: 拒绝好友申请
- **请求参数**:
  ```json
  {
    "id": 123 // 好友申请ID
  }
  ```
- **响应**:
  ```json
  {
    "code": 0,
    "data": true,
    "message": "ok"
  }
  ```

### 3. 获取好友列表
- **URL**: `/friends/list`
- **方法**: GET
- **描述**: 获取当前用户的好友列表
- **响应**:
  ```json
  {
    "code": 0,
    "data": [
      {
        "id": 1,
        "userId": 1001,
        "friendId": 1002,
        "status": 1,
        "createTime": "2023-01-01T00:00:00.000+00:00",
        "updateTime": "2023-01-01T00:00:00.000+00:00"
      }
    ],
    "message": "ok"
  }
  ```

### 4. 删除好友
- **URL**: `/friends/delete`
- **方法**: POST
- **描述**: 删除好友关系
- **请求参数**:
  ```json
  1002 // 好友用户ID
  ```
- **响应**:
  ```json
  {
    "code": 0,
    "data": true,
    "message": "ok"
  }
  ```

### 5. 检查是否为好友
- **URL**: `/friends/check`
- **方法**: GET
- **描述**: 检查与指定用户是否为好友关系
- **请求参数**:
  - friendUserId: 好友用户ID
- **响应**:
  ```json
  {
    "code": 0,
    "data": {
      "id": 1,
      "userId": 1001,
      "friendId": 1002,
      "status": 1,
      "createTime": "2023-01-01T00:00:00.000+00:00",
      "updateTime": "2023-01-01T00:00:00.000+00:00"
    },
    "message": "ok"
  }
  ```

## 好友申请接口 (FriendRequestsController)

### 1. 发送好友申请
- **URL**: `/friendRequests/send`
- **方法**: POST
- **描述**: 向指定用户发送好友申请
- **请求参数**:
  ```json
  {
    "friendUserId": 1002, // 目标用户ID
    "message": "你好，我想加你为好友" // 申请备注信息
  }
  ```
- **响应**:
  ```json
  {
    "code": 0,
    "data": true,
    "message": "ok"
  }
  ```

### 2. 获取好友申请列表
- **URL**: `/friendRequests/list`
- **方法**: GET
- **描述**: 获取当前用户收到的好友申请列表
- **响应**:
  ```json
  {
    "code": 0,
    "data": [
      {
        "id": 1,
        "fromUserId": 1002,
        "toUserId": 1001,
        "message": "你好，我想加你为好友",
        "status": 0,
        "createTime": "2023-01-01T00:00:00.000+00:00",
        "updateTime": "2023-01-01T00:00:00.000+00:00"
      }
    ],
    "message": "ok"
  }
  ```

## 状态码说明

- 0: 成功
- 40000: 请求参数错误
- 40100: 未登录
- 其他: 业务错误

## 注意事项

1. 所有接口都需要用户登录后才能调用
2. 好友关系状态：0-已删除, 1-正常好友
3. 好友申请状态：0-待处理, 1-已同意, 2-已拒绝