// *** Make sure that comments follow the 'Single line comment format'. Multi line comments are not supported. ***
// Check here for more details: https://github.com/patka/cassandra-migration

CREATE KEYSPACE if not exists integrations WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'}  AND durable_writes = true;

DROP TABLE if exists integrations.service_request;
DROP TABLE if exists integrations.audit_session;
DROP TABLE if exists integrations.connection;
DROP TABLE if exists integrations.provider;
DROP TABLE if exists integrations.rules;
DROP TABLE if exists integrations.stage_entities;


CREATE TABLE if not exists integrations.stage_entities (
//
// Description: - Unique id for a stage transaction - generated one
// Example: -
// FDP Mapped Field: -
//
    entity_id    timeuuid,

//
//  Description: - Company Id
//  FDP Mapped Field: -
//
    realm_id    bigint,


///////////////////////
// Payload, ids, refs
//////////////////////

//
//  Description: - V4 payload stage entities from original_payload - current stage entity
//  FDP Mapped Field: -
//
    payload    text,

//
//  Description: - Raw payload for stage entities - never gets updated
//  FDP Mapped Field: -
//
    original_payload    text,


/////////////////
// Transaction
////////////////

//
//  Description: - actual posted date of transaction
//
    posted_date    timestamp,

//
//  Description: - All resolved QBO entities after transaction is posted to QBO
//  Example: - Split transaction,
//
    entities_out    text,


/////////////////
// Receipt
////////////////

//
//  Description: - transaction source - Bank Feed/ PayPal/ ReceiptCapture etc
//
    transaction_source    varchar,

//
//  Description: - For receipt capture match against stage transaction
//  Example: -
//
    linked_stage_entities    set<varchar>,

//
//  Description: - e.g. -To identify the receipt capture match
//  Example: -
//
    is_primary    boolean,

//
//  Description: - Potential match transactions in QBO
//  Example: -
//
    suggested_qbo_matches    set<varchar>,

//
//  Description: - Status of the receipt processing - processed, pending, error, missing_data
//  Example: -
//
    receipt_processing_state    varchar,

//
//  Description: - To mark if a transaction is a duplicate, marked by user
//  Example: -
//
    user_marked_duplicate    boolean,

//
//  Description: - Rule applied to a transaction from stage
//  Example: -
//
    applied_rule_ids    set<varchar>,

//
//  Description: - Identifies the session id of transaction which was responsible for transaction create/update
//  Example: -
//
    audit_session_id    timeuuid,

//
//  Description: - Identifier to track transaction across multiple services
//  Example: - Logged in API Gateway, Stagging, QBO, IUS, Edge etc
//
    intuit_tid    varchar,

//
//  Description: - Application which created this record
//  Example: - app id - Intuit.qboplatform.sbgintegrations.integrations
//
    created_app_id    varchar,

//
//  Description: - Application which updated this record
//  Example: -
//
    updated_app_id    varchar,

//
//  Description: - User id of the company
//  Example: -
//
    created_user_id    varchar,

//
//  Description: - User id of the company
//  Example: -
//
    updated_user_id    varchar,

//
//  Description: - Date the record was created
//
    created_date    timestamp,

//
//  Description: - Date the record was updated
//
    updated_date    timestamp,


    PRIMARY KEY ((realm_id), entity_id)
)
WITH CLUSTERING ORDER BY (entity_id ASC);

CREATE TYPE if not exists integrations.provider_settings (
    entity_id uuid,
    name   varchar,
    label  varchar,
    helptext varchar,
    display_order int,
    choices  varchar
);

CREATE TABLE if not exists integrations.provider (
    entity_id timeuuid,
    provider_app_id varchar,
    provider_name varchar,
    datafeed_source varchar,
    status varchar,
    provider_url varchar,
    created_date timestamp,
    updated_date    timestamp,
    created_app_id  varchar,
    updated_app_id  varchar,
    created_user_id varchar,
    updated_user_id varchar,
    provider_settings set<frozen <provider_settings>>,
    PRIMARY KEY ((entity_id))
);


CREATE TYPE if not exists integrations.connection_settings (
    id timeuuid,
    provider_settings_id timeuuid,
    value varchar,
    created_date timestamp,
    updated_date    timestamp,
    created_app_id  varchar,
    updated_app_id  varchar,
    created_user_id varchar,
    updated_user_id varchar,
);

CREATE TYPE if not exists integrations.connection_account_details (

//
//  Description: - unique id for the connection account, system generated
//  Example: -
//
    id uuid,

//
//  Description: - chart of account id
//  Example: -
//
    ledger_account_id varchar,

//
//  Description: - bank account id, comes from fdp
//  Example: - urn:account:fdp::accountid:ca247d80-885b-11e7-944f-0050569f54f1
//
    fdp_account_id varchar,


//
//  Description: - Combination of Type and Status of the connection account. This will be split later into status and type
//  Example: - ACTIVE, DISCONNECTED etc.
//
    account_type varchar,

//
//  Description: -  last 4 digit of fi account number
//  Example: -
//
    account_number varchar,

//
//  Description: -  name of the account, user defined
//  Example: -  my paypal
//
    account_nickname varchar,

//
//  Description: -   name of the account given by fi
//  Example: -
//
    account_name varchar,

//
//  Description: -  date when the last refresh was attempted, this attempt could succeed or fail
//  Example: -
//
    last_attempted_refresh_date timestamp,


//
//  Description: -  primary purpose of the account
//  Example: -  business, personal or both
//
    primary_purpose varchar,

//
//  Description: -  subtype of the account
//  Example: -  savings, checkings etc.
//
    subtype varchar,

//
//  Description: -  indicates whether the connection account is visible on the ui or not
//  Example: -
//
    visible boolean,

//
//  Description: -  account balance
//  Example: -
//
    open_balance decimal,

//
//  Description: -  currency of the fi account
//  Example: -
//
    iso_currency varchar,

//
//  Description: -  field to indicate the account was deleted
//  Example: -
//
    deleted boolean,

//
//  Description: -  time when the balance was last updated
//  Example: -
//
    balance_as_of timestamp,

//
//  Description: -  number of transactions last updated
//  Example: -
//
    num_txn_last_update int,

//
//  Description: -  number of transactions to be reviewed
//  Example: -
//
    num_txn_to_review int,

//
//  Description: -  number of transactions in that are unmatched
//  Example: -
//
    num_txn_unmatched_count int,

//
//  Description: -  error that in the occurred last in the account
//  Example: -
//
    last_err_code varchar,

//
//  Description: - time when the last error occurred during account refresh/sync
//  Example: -
//
    last_err_time timestamp,

//
//  Description: - date when the connection account record was created
//  Example: -
//
    created_date timestamp,

//
//  Description: - date when the connection account record was updated
//  Example: -
//
    updated_date    timestamp,

//
//  Description: - application that created the connection account
//  Example: -
//
    created_app_id  varchar,

//
//  Description: - application that updated the connection account
//  Example: -
//
    updated_app_id  varchar,

);

CREATE TABLE if not exists integrations.connection (
//
//  Description: - Unique id for connection - system generated
//
    id timeuuid,

//
//  Description: - company id
//
    realm_id bigint,

//
//  Description: - reference to provider table
//  Example: - bank, paypal etc.
//
    provider_id   timeuuid,

//
//  Description: - provider id from fdp
//  Example: - bank, paypal etc.
//
    fdp_provider_id varchar,

//
//  Description: - latest correlation id received by fdp jms listener
//  Example: - 731e7ed8-fb94-4616-ace5-b10da7337273
//
    fdp_correlation_id varchar,

//
//  Description: - Identifier for subscription of the app with QBO
//  Example: - app-connect generates an id for paypal
//
    app_subscription_id varchar,

//
//  Description: - connection status
//  Example: - CONNECTED, DISCONNECTED etc.
//
    status varchar,

// Description: - used for storing credential set id and paypal account id
// Example: -
//
    fi_credset_id varchar,

// Description: - id of the fi
// Example: - bank, null for 3p apps
//
    fi_id varchar,

//
//  Description: -
//  Example: -
//
    connection_settings set<frozen <connection_settings>>,

//
//  Description: - connection accounts for this connection
//  Example: -
//
    connection_account_details set<frozen <connection_account_details>>,

//
//  Description: - date when connection record was created
//  Example: -
//
    created_date timestamp,

//
//  Description: - date when the connection record was updated
//  Example: -
//
    updated_date    timestamp,

//
//  Description: - application that created this connection record
//  Example: -
//
    created_app_id  varchar,

//
//  Description: - application that updated this connection record
//  Example: -
//
    updated_app_id  varchar,

//
//  Description: - payload for connections
//  Example: -
//
    payload text,


    PRIMARY KEY ((realm_id), id)
);

CREATE TABLE if not exists integrations.rules (
    rule_id uuid,
    realm_id bigint,
    rule_name varchar,
    edit_sequence varchar,
    rule_order varchar,
    is_and_rule boolean,
    is_system_created boolean,
    is_deleted boolean,
    rule_actions set<varchar>,
    rule_conditions  set<varchar>,
    created_date timestamp,
    updated_date timestamp,
    created_app_id  varchar,
    updated_app_id  varchar,
    created_user_id varchar,
    updated_user_id varchar,
    payload text,
    PRIMARY KEY ((realm_id), rule_id)
);

CREATE TABLE if not exists integrations.service_request (
    request_id varchar,
    realm_id bigint,
    operation_name varchar,
    entity_id varchar,
    created_date timestamp,
    updated_date timestamp,
    PRIMARY KEY ((realm_id), request_id)
);

CREATE TYPE if not exists integrations.ledger_account_audit_session (
//
//  Description: - chart of account id
//
    ledger_account_id varchar,

//
//  Description: - account balance
//
    open_balance decimal,

//
//  Description: - time when the balance was last updated
//
    balance_as_of    timestamp,

//
//  Description: -  date when the last refresh was attempted, this attempt could succeed or fail
//
    last_attempted_refresh_date    timestamp,

//
//  Description: - number of transactions last updated
//
    num_txn_last_update int,

//
//  Description: - error that occurred last in the account
//  Example: - 0 for success, 3 for timeout error, 103 for invalid credential error
//
    last_err_code varchar,

//
//  Description: - error message shown to user to explain last_err_code
//  Example: - "Please edit your login information in QuickBooks Online"
//
    last_err_message varchar
);

//
//  Description: - log of each fdp transaction download session
//
CREATE TABLE if not exists integrations.audit_session (
//
//  Description: - unique id for the connection account, system generated
//
    id timeuuid,

//
//  Description: - company id
//
    realm_id bigint,

//
//  Description: - log of all accounts that were updated this session
//
    account_sessions set<frozen <ledger_account_audit_session>>,

//
//  Example: - FDP_INTERACTIVE_INITIAL_UPDATE, FDP_INTERACTIVE_MANUAL_UPDATE, FDP_BATCH_UPDATE
//
    session_type varchar,

//
//  Example: - TEKNOWLEDGE, FDP
//
    connection_type varchar,

//
//  Description: - start time of fdp transaction download session
//
    start_time timestamp,

//
//  Description: - end time of fdp transaction download session
//
    end_time timestamp,

//
//  Description: - status of fdp transaction download session
//  Example: - IN_PROGRESS, DONE, DONE_WITH_PARTIAL_DATA, ERROR
//
    completion_status varchar,

//
//  Description: - error code sent by fdp for this session
//  Example: - 0 for success, 3 for timeout error, 103 for invalid credential error
//
    error_number varchar,

//
//  Description: - error message shown to user to explain error_number
//  Example: - "Please edit your login information in QuickBooks Online"
//
    status_message varchar,

//
//  Description: - payload for audit session
//  Example: -
//
    payload text,

    created_user_id varchar,
    created_app_id  varchar,
    PRIMARY KEY ((realm_id), id)
);