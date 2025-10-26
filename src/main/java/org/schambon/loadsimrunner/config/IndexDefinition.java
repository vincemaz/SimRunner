package org.schambon.loadsimrunner.config;

import java.util.concurrent.TimeUnit;

import org.bson.Document;

import com.mongodb.client.model.Collation;
import com.mongodb.client.model.CollationAlternate;
import com.mongodb.client.model.CollationCaseFirst;
import com.mongodb.client.model.CollationMaxVariable;
import com.mongodb.client.model.CollationStrength;
import com.mongodb.client.model.Collation.Builder;
import com.mongodb.client.model.IndexOptions;
import org.schambon.loadsimrunner.DocumentGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexDefinition {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexDefinition.class);

    private final Document keys;
    private final IndexOptions options;

    /**
     * Creates a SimRunner index definition.
     * @param indexConfig the Document representing the index configuration.
     */
    public IndexDefinition(Document indexConfig){
        Object keyFieldValue = indexConfig.get("key");
        // index with options or with
        if(keyFieldValue instanceof Document) {
            this.keys = (Document) keyFieldValue;
            this.options = getOptions(indexConfig);
        }
        // legacy or simple syntax
        else {
            this.keys = indexConfig;
            this.options = new IndexOptions();;
        }
    }

    /**
     * Retrieves the index keys.
     *
     * @return a Document representing the index keys.
     */
    public Document getKeys(){
        return this.keys;
    }

    /**
     * @return the index options.
     */
    public IndexOptions getOptions(){
        return this.options;
    }

    private IndexOptions getOptions(Document indexConfig) {
        IndexOptions indexOptions = new IndexOptions();
        ConfigHelper configHelper = new ConfigHelper(indexConfig);
        for (var entry : indexConfig.entrySet()) {
            switch (entry.getKey()) {
            case "unique":
                indexOptions.unique(configHelper.getValue(entry.getKey(), Boolean.class));
                break;
            case "name":
                indexOptions.name(configHelper.getValue(entry.getKey(), String.class));
                break;
            case "sparse":
                indexOptions.sparse(configHelper.getValue(entry.getKey(), Boolean.class));
                break;
            case "hidden":
                indexOptions.hidden(configHelper.getValue(entry.getKey(), Boolean.class));
                break;
            case "expireAfterSeconds":
                indexOptions.expireAfter(configHelper.getValue(entry.getKey(), Integer.class).longValue(), TimeUnit.SECONDS);
                break;
            case "partialFilterExpression":
                indexOptions.partialFilterExpression(configHelper.getValue(entry.getKey(), Document.class));
                break;
            case "storageEngine":
                indexOptions.storageEngine(configHelper.getValue(entry.getKey(), Document.class));
                break;
            case "weights":
                indexOptions.weights(configHelper.getValue(entry.getKey(), Document.class));
                break;
            case "default_language":
                indexOptions.defaultLanguage(configHelper.getValue(entry.getKey(), String.class));
                break;
            case "language_override":
                indexOptions.languageOverride(configHelper.getValue(entry.getKey(), String.class));
                break;
            case "textIndexVersion":
                indexOptions.textVersion(configHelper.getValue(entry.getKey(), Integer.class));
                break;
            case "2dsphereIndexVersion":
                indexOptions.sphereVersion(configHelper.getValue(entry.getKey(), Integer.class));
                break;
            case "bits":
                indexOptions.bits(configHelper.getValue(entry.getKey(), Integer.class));
                break;
            case "min":
                var minValue = entry.getValue();
                if (minValue instanceof Integer){
                   minValue = ((Integer) minValue).doubleValue();
                }
                indexOptions.min((Double)minValue);
                break;
            case "max":
                var maxValue = entry.getValue();
                if (maxValue instanceof Integer){
                    maxValue = ((Integer) maxValue).doubleValue();
                }
                indexOptions.max((Double)maxValue);
                break;
            case "wildcardProjection":
                indexOptions.wildcardProjection(configHelper.getValue(entry.getKey(), Document.class));
                break;
            case "background":
                indexOptions.background(configHelper.getValue(entry.getKey(), Boolean.class));
                break;
            case "collation":
                indexOptions.collation(getCollation(configHelper.getValue(entry.getKey(), Document.class)));
                break;
            case "key":
                // ignore - do nothing
                break;
            default:
                LOGGER.warn("Unrecognized index option: '{}'", entry.getKey());
                break;
            }
        }
        return indexOptions;
    }

    /**
     * @param collationConfig the configuration document containing the collation options.
     * @return the collation options.
     */
    private Collation getCollation(Document collationConfig) {
        Builder builder = Collation.builder();
        for (var entry : collationConfig.entrySet()) {
            switch (entry.getKey()) {
                case "locale":
                    builder.locale((String)entry.getValue());
                    break;
                case "caseLevel":
                    builder.caseLevel((boolean)entry.getValue());
                    break;
                case "caseFirst":
                    builder.collationCaseFirst(CollationCaseFirst.fromString((String)entry.getValue()));
                    break;
                case "strength":
                    builder.collationStrength(CollationStrength.fromInt((Integer)entry.getValue()));
                    break;
                case "numericOrdering":
                    builder.numericOrdering((boolean)entry.getValue());
                    break;
                case "normalization":
                    builder.normalization((boolean)entry.getValue());
                    break;
                case "alternate":
                    builder.collationAlternate(CollationAlternate.fromString((String)entry.getValue()));
                    break;
                case "maxVariable":
                    builder.collationMaxVariable(CollationMaxVariable.fromString((String)entry.getValue()));
                    break;
                case "backwards":
                    builder.backwards((boolean)entry.getValue());
                    break;

                default:
                    // TODO log warning message

                    break;
            }
        }
        return builder.build();
    }

    public static void main(String[] args) {
        String config = "{ 'key':{'field1':1, 'field2': -1}, 'name': 'myIndex'}";
        Document document = Document.parse(config);
        var indexDef = new IndexDefinition(document);
        System.out.println(indexDef.getKeys().toJson());
        System.out.println(indexDef.getOptions().toString());

        config = "{'field1':1, 'field2': -1}";
        document = Document.parse(config);
        new IndexDefinition(document);
    }


}

