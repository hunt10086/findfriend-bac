create table daylight.user
(
    id            bigint auto_increment comment '主键'
        primary key,
    user_name     varchar(255)                       null comment '昵称',
    user_account  varchar(255)                       not null comment '登录账号',
    avatar_url    varchar(255)                       null comment '头像',
    gender        tinyint  default 0                 null comment '性别（0-未知 1-男 2-女）',
    user_password varchar(255)                       not null comment '密码',
    phone         varchar(20)                        null comment '电话',
    email         varchar(255)                       null comment '邮箱',
    user_status   int      default 0                 null comment '用户状态 0 - 正常',
    create_time   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete     tinyint  default 0                 null comment '是否删除 0 - 未删除 1 - 删除',
    user_role     tinyint  default 0                 null comment '用户角色 0 - 普通用户 1 - 管理员',
    tags          varchar(1024)                      null comment '标签列表',
    profile       varchar(512)                       null comment '个人简介',
    latitude      double                             null comment '经度',
    longitude     double                             null comment '纬度',
    constraint email_unique
        unique (email),
    constraint phone_unique
        unique (phone),
    constraint userAccount_unique
        unique (user_account)
)
    comment '用户表' collate = utf8mb4_unicode_ci;


create table daylight.user_comment
(
    user_id     bigint                             not null comment '发表用户Id',
    blog_id     bigint                             not null comment '博客id',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    is_delete   tinyint  default 0                 not null comment '是否删除',
    content     varchar(256)                       not null comment '评论'
)
    comment '用户评论';

create table daylight.user_team
(
    id          bigint auto_increment comment 'id'
        primary key,
    user_id     bigint                             null comment '用户id',
    team_id     bigint                             null comment '队伍id',
    join_time   datetime                           null comment '加入时间',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    is_delete   tinyint  default 0                 not null comment '是否删除'
)
    comment '用户队伍关系';


create table daylight.team_chat_message
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    team_id     bigint                             not null comment '队伍ID',
    user_id     bigint                             not null comment '发送者用户ID',
    content     varchar(500)                       not null comment '消息内容',
    create_time datetime default CURRENT_TIMESTAMP not null comment '发送时间'
)
    comment '队伍聊天消息表';

create index idx_team_id
    on daylight.team_chat_message (team_id);

create index idx_user_id
    on daylight.team_chat_message (user_id);


create table daylight.team
(
    id          bigint auto_increment comment 'id'
        primary key,
    team_name   varchar(128)                       not null comment '队伍名称',
    description varchar(512)                       null comment '描述',
    max_num     int      default 1                 not null comment '最大人数',
    user_id     bigint                             null comment '用户id',
    status      int      default 0                 not null comment '0 - 无加密  1- 加密',
    password    varchar(128)                       null comment '密码',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    is_delete   tinyint  default 0                 not null comment '是否删除',
    icon        varchar(512)                       null comment '队伍头像'
)
    comment '队伍';

create table daylight.blog
(
    id          bigint auto_increment comment 'id'
        primary key,
    title       varchar(32)                        not null comment '标题',
    passage     varchar(2048)                      not null comment '文章',
    user_id     bigint                             not null comment '发表用户Id',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null comment '更新时间',
    is_delete   tinyint  default 0                 not null comment '是否删除',
    kind        varchar(16)                        null comment '文章类型',
    praise      int                                null comment '文章获赞'
)
    comment '博客文章';

create table daylight.friend_messages
(
    id              bigint auto_increment comment '消息ID'
        primary key,
    sender_id       bigint                             not null comment '发送者ID',
    receiver_id     bigint                             not null comment '接收者ID',
    message_content varchar(512)                       null comment '消息内容',
    send_time       datetime default CURRENT_TIMESTAMP null comment '发送时间',
    status          int      default 1                 null comment '消息状态：0-已删除，1-正常'
)
    comment '好友消息表' collate = utf8mb4_unicode_ci;

create index idx_receiver_status
    on daylight.friend_messages (receiver_id);

create index idx_send_time
    on daylight.friend_messages (send_time);

create index idx_sender_receiver
    on daylight.friend_messages (sender_id, receiver_id);

create table daylight.friend_requests
(
    id           bigint auto_increment
        primary key,
    from_user_id bigint                             not null,
    to_user_id   bigint                             not null,
    message      varchar(512)                       null comment '申请备注',
    status       tinyint  default 0                 null comment '0-待处理, 1-已同意, 2-已拒绝',
    create_time  datetime default CURRENT_TIMESTAMP null,
    update_time  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
)
    comment '好友申请表';

create index idx_from_user
    on daylight.friend_requests (from_user_id);

create index idx_to_user
    on daylight.friend_requests (to_user_id);

create table daylight.friends
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    user_id     bigint                             not null comment '用户ID',
    friend_id   bigint                             not null comment '好友ID',
    status      tinyint  default 1                 null comment '好友状态：0-已删除, 1-正常好友, 2-拉黑',
    create_time datetime default CURRENT_TIMESTAMP null comment '成为好友时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint uk_user_friend
        unique (user_id, friend_id)
)
    comment '好友关系表' collate = utf8mb4_unicode_ci;

create index idx_friend_id
    on daylight.friends (friend_id);

create index idx_user_id
    on daylight.friends (user_id);

