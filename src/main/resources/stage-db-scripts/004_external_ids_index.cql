// Add columns that are added through fit
ALTER SEARCH INDEX schema on integrations.stage_entities ADD fields.field[@name='external_transaction_id', @indexed='true', @type='StrField'];
ALTER SEARCH INDEX schema on integrations.stage_entities ADD fields.field[@name='fi_transaction_id', @indexed='true', @type='StrField'];

// Reload the schema again
RELOAD SEARCH INDEX ON integrations.stage_entities ;

// Reindex all documents
REBUILD SEARCH INDEX ON integrations.stage_entities ;
