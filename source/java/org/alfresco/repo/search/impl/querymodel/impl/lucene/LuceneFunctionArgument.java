package org.alfresco.repo.search.impl.querymodel.impl.lucene;

import java.util.Map;

import org.alfresco.repo.search.impl.querymodel.Argument;
import org.alfresco.repo.search.impl.querymodel.Function;
import org.alfresco.repo.search.impl.querymodel.impl.BaseFunctionArgument;

/**
 * @author andyh
 *
 */
public class LuceneFunctionArgument extends BaseFunctionArgument
{

    /**
     * @param name String
     * @param function Function
     * @param arguments Map<String, Argument>
     */
    public LuceneFunctionArgument(String name, Function function, Map<String, Argument> arguments)
    {
        super(name, function, arguments);
    }

}
