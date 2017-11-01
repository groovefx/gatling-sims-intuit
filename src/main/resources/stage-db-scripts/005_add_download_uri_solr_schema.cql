
// Add columns that are added through fit
ALTER SEARCH INDEX schema on integrations.stage_entities ADD fields.field[@name='download_uri', @indexed='true', @type='StrField'];

// Reload the schema again
RELOAD SEARCH INDEX ON integrations.stage_entities ;

// Reload the schema again
RELOAD SEARCH INDEX ON integrations.stage_entities ;

// Reindex all documents
REBUILD SEARCH INDEX ON integrations.stage_entities ;
REBUILD SEARCH INDEX ON integrations.stage_entities ;