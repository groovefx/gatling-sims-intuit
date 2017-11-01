// Create Solr core with with the specified columns only
CREATE SEARCH INDEX if not exists ON integrations.stage_entities WITH COLUMNS payload, entity_id, realm_id, is_primary, posted_date, created_app_id;

// Add missing field types
ALTER SEARCH INDEX schema on integrations.stage_entities ADD types.fieldType[@class='com.datastax.bdp.search.solr.core.types.DecimalStrField',@name='DecimalStrField'] ;
ALTER SEARCH INDEX schema on integrations.stage_entities ADD types.fieldType[@class='org.apache.solr.schema.StrField',@name='StrField'] ;

// Update the Payload type to StrField
ALTER SEARCH INDEX schema on integrations.stage_entities SET field[@name='payload']@type='StrField';

// Make sure that the schema is reloaded before we proceed
RELOAD SEARCH INDEX ON integrations.stage_entities ;

// Add columns that are added through fit
ALTER SEARCH INDEX schema on integrations.stage_entities ADD fields.field[@name='status', @indexed='true', @type='StrField'];
ALTER SEARCH INDEX schema on integrations.stage_entities ADD fields.field[@name='connection_account_id', @indexed='true', @type='StrField'];
ALTER SEARCH INDEX schema on integrations.stage_entities ADD fields.field[@name='transaction_date', @indexed='true', @type='TrieDateField'];
ALTER SEARCH INDEX schema on integrations.stage_entities ADD fields.field[@name='amount', @indexed='true', @type='DecimalStrField'];

// Reload the schema again
RELOAD SEARCH INDEX ON integrations.stage_entities ;

// Add the FitTransformer to config
ALTER SEARCH INDEX CONFIG ON integrations.stage_entities ADD fieldInputTransformer[@name='dse',@class='com.intuit.sbg.integrations.solr.proxy.FitTransformer'] ;
ALTER SEARCH INDEX CONFIG ON integrations.stage_entities SET updateHandler.autoSoftCommit.maxTime = 1000;

// Reload the schema again
RELOAD SEARCH INDEX ON integrations.stage_entities ;

// Reindex all documents
REBUILD SEARCH INDEX ON integrations.stage_entities ;