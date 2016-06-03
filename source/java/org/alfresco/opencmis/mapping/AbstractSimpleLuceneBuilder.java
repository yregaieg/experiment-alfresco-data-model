package org.alfresco.opencmis.mapping;

import java.io.Serializable;

import org.alfresco.repo.search.adaptor.lucene.AnalysisMode;
import org.alfresco.repo.search.adaptor.lucene.LuceneFunction;
import org.alfresco.repo.search.adaptor.lucene.LuceneQueryParserAdaptor;
import org.alfresco.repo.search.impl.querymodel.PredicateMode;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.namespace.QName;

/**
 * Common support for lucene query building.
 * 
 * @author davidc
 */
public abstract class AbstractSimpleLuceneBuilder extends BaseLuceneBuilder
{
    /**
     * Construct
     */
    protected AbstractSimpleLuceneBuilder()
    {
        super();
    }

    protected abstract String getValueAsString(Serializable value);
    
    protected String getRangeMax()
    {
        return "\uFFFF";
    }
    
    protected String getRangeMin()
    {
        return "\u0000";
    }
    
    protected abstract DataTypeDefinition getInDataType();
    
    protected abstract QName getQNameForExists();

    @Override
    public <Q, S, E extends Throwable> Q buildLuceneEquality(LuceneQueryParserAdaptor<Q, S, E> lqpa, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws E
    {
        return lqpa.getFieldQuery(getLuceneFieldName(), getValueAsString(value), AnalysisMode.IDENTIFIER, luceneFunction);
    }
    
    @Override
    public <Q, S, E extends Throwable> Q buildLuceneExists(LuceneQueryParserAdaptor<Q, S, E> lqpa, Boolean not) throws E
    {
        if (not)
        {
            return lqpa.getFieldQuery("ISNULL", getQNameForExists().toString(), AnalysisMode.DEFAULT, LuceneFunction.FIELD);
        }
        else
        {
            return lqpa.getFieldQuery("ISNOTNULL", getQNameForExists().toString(), AnalysisMode.DEFAULT, LuceneFunction.FIELD);
        }
    }

    @Override
    public <Q, S, E extends Throwable> Q buildLuceneGreaterThan(LuceneQueryParserAdaptor<Q, S, E> lqpa, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws E
    {
        String field = getLuceneFieldName();
        String stringValue = getValueAsString(value);
        return lqpa.getRangeQuery(field, stringValue, getRangeMax(), false, true, AnalysisMode.IDENTIFIER, luceneFunction);
    }

    @Override
    public <Q, S, E extends Throwable> Q buildLuceneGreaterThanOrEquals(LuceneQueryParserAdaptor<Q, S, E> lqpa, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws E
    {
        String field = getLuceneFieldName();
        String stringValue = getValueAsString(value);
        return lqpa.getRangeQuery(field, stringValue, getRangeMax(), true, true, AnalysisMode.IDENTIFIER, luceneFunction);
    }

    @Override
    public <Q, S, E extends Throwable> Q buildLuceneLessThan(LuceneQueryParserAdaptor<Q, S, E> lqpa, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws E
    {
        String field = getLuceneFieldName();
        String stringValue = getValueAsString(value);
        return lqpa.getRangeQuery(field, getRangeMin(), stringValue, true, false, AnalysisMode.IDENTIFIER, luceneFunction);
    }

    @Override
    public <Q, S, E extends Throwable> Q buildLuceneLessThanOrEquals(LuceneQueryParserAdaptor<Q, S, E> lqpa, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws E
    {
        String field = getLuceneFieldName();
        String stringValue = getValueAsString(value);
        return lqpa.getRangeQuery(field, getRangeMin(), stringValue, true, true, AnalysisMode.IDENTIFIER, luceneFunction);
    }

    @Override
    public <Q, S, E extends Throwable> Q buildLuceneLike(LuceneQueryParserAdaptor<Q, S, E> lqpa, Serializable value, Boolean not) throws E
    {
        String field = getLuceneFieldName();
        String stringValue = getValueAsString(value);

        Q q =  lqpa.getLikeQuery(field, stringValue, AnalysisMode.IDENTIFIER);
        if (not)
        {
            q = lqpa.getNegatedQuery(q);
        }
        return q;
    }

    @Override
    public <Q, S, E extends Throwable> String getLuceneSortField(LuceneQueryParserAdaptor<Q, S, E> lqpa) throws E
    {
        return getLuceneFieldName();
    }
}
