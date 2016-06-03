package org.alfresco.opencmis.dictionary;

import java.util.Collection;

import org.alfresco.opencmis.CMISUtils;
import org.alfresco.opencmis.mapping.CMISMapping;
import org.alfresco.service.cmr.dictionary.ClassDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ISO9075;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ItemTypeDefinitionImpl;

public class ItemTypeDefinitionWrapper extends ShadowTypeDefinitionWrapper
{
    private static final long serialVersionUID = 1L;

    private ItemTypeDefinitionImpl typeDef;
    private ItemTypeDefinitionImpl typeDefInclProperties;
    private DictionaryService dictionaryService;

    public ItemTypeDefinitionWrapper(CMISMapping cmisMapping, PropertyAccessorMapping accessorMapping, 
            PropertyLuceneBuilderMapping luceneBuilderMapping, String typeId, DictionaryService dictionaryService, ClassDefinition cmisClassDef)
    {
        this.dictionaryService = dictionaryService;
        alfrescoName = cmisClassDef.getName();
        alfrescoClass = cmisMapping.getAlfrescoClass(alfrescoName);

        typeDef = new ItemTypeDefinitionImpl();

        typeDef.setBaseTypeId(BaseTypeId.CMIS_ITEM);
        typeDef.setId(typeId);
        typeDef.setLocalName(alfrescoName.getLocalName());
        typeDef.setLocalNamespace(alfrescoName.getNamespaceURI());

        if (BaseTypeId.CMIS_ITEM.value().equals(typeId) )
        {
            typeDef.setQueryName(ISO9075.encodeSQL(typeId));
            typeDef.setParentTypeId(null);
        }  
        else
        {
            typeDef.setQueryName(ISO9075.encodeSQL(cmisMapping.buildPrefixEncodedString(alfrescoName)));
            QName parentQName = cmisMapping.getCmisType(cmisClassDef.getParentName());
            if(parentQName != null)
            {         
                typeDef.setParentTypeId(cmisMapping.getCmisTypeId(BaseTypeId.CMIS_ITEM, parentQName));
            }
        }

        typeDef.setDisplayName(null);
        typeDef.setDescription(null);

        if (BaseTypeId.CMIS_ITEM.value().equals(typeId) )
        {
        	typeDef.setIsCreatable(false);   // cmis:item is abstract
        	 // TEMP work around for select * from cmis:item which lists folders and files
        	typeDef.setIsQueryable(false);
        }
        else
        {
        	typeDef.setIsCreatable(true);
        	typeDef.setIsQueryable(true);
        }
      
        typeDef.setIsFulltextIndexed(true);
        typeDef.setIsControllablePolicy(true);
        typeDef.setIsControllableAcl(true);
        typeDef.setIsIncludedInSupertypeQuery(cmisClassDef.getIncludedInSuperTypeQuery());
        typeDef.setIsFileable(false);

        typeDefInclProperties = CMISUtils.copy(typeDef);
        setTypeDefinition(typeDef, typeDefInclProperties);

        createOwningPropertyDefinitions(cmisMapping, accessorMapping, luceneBuilderMapping, dictionaryService, cmisClassDef);
        createActionEvaluators(accessorMapping, BaseTypeId.CMIS_ITEM);
    }
    
    @Override
    public void updateDefinition(DictionaryService dictionaryService)
    {
        TypeDefinition typeDef = dictionaryService.getType(alfrescoName);

        if (typeDef != null)
        {
            setTypeDefDisplayName(typeDef.getTitle(dictionaryService));
            setTypeDefDescription(typeDef.getDescription(dictionaryService));
        }
        else
        {
            super.updateDefinition(dictionaryService);
        }
        
        updateTypeDefInclProperties();
    }
    
    @Override
    public PropertyDefinitionWrapper getPropertyById(String propertyId)
    {
        updateProperty(dictionaryService, propertiesById.get(propertyId));
        return propertiesById.get(propertyId);
    }

    @Override
    public Collection<PropertyDefinitionWrapper> getProperties()
    {
        updateProperties(dictionaryService);
        return propertiesById.values();
    }
    
    @Override
    public Collection<PropertyDefinitionWrapper> getProperties(boolean update)
    {
        if (update)
        {
            return getProperties();
        }
        else
        {
            return propertiesById.values();
        }
    }
}