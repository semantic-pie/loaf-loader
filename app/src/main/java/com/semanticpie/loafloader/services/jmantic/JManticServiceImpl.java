package com.semanticpie.loafloader.services.jmantic;

import com.semanticpie.loafloader.services.sync.ResourceAlreadyExistException;
import lombok.extern.slf4j.Slf4j;
import org.ostis.api.context.DefaultScContext;
import org.ostis.scmemory.model.element.edge.EdgeType;
import org.ostis.scmemory.model.element.node.NodeType;
import org.ostis.scmemory.model.exception.ScMemoryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@Service
public class JManticServiceImpl implements JManticService {

    private final DefaultScContext context;

    @Autowired
    public JManticServiceImpl(DefaultScContext context) {
        this.context = context;
    }

    @Override
    public void putFile(String hash, MultipartFile multipartFile) throws ScMemoryException {
        try {
            createResource(hash, multipartFile);
        } catch (ScMemoryException e) {
            throw new ScMemoryException(e);
        }

    }

    private void createResource(String hash, MultipartFile multipartFile) throws ScMemoryException {
        log.info("try createResource {}", hash);
        if (context.findKeynode(hash).isPresent()) {
            throw new ResourceAlreadyExistException(hash);
        }
        log.info("createResource {}", hash);

        var resource = context.resolveKeynode(hash, NodeType.CONST);
        var noRolFormat = context.resolveKeynode("nrel_format", NodeType.CONST_NO_ROLE);
        var classFormat = context.resolveKeynode("format", NodeType.CONST_CLASS);
        var resourceFormat = context.resolveKeynode(toContentType(multipartFile.getContentType()), NodeType.CONST_CLASS);
        context.createEdge(EdgeType.ACCESS_CONST_POS_PERM, classFormat, resourceFormat);
        var edge = context.resolveEdge(resource, EdgeType.D_COMMON_VAR, resourceFormat);
        context.resolveEdge(noRolFormat, EdgeType.ACCESS_VAR_POS_PERM, edge);
    }

    private String toContentType(String contentType) {
        if (contentType != null && !contentType.isEmpty())
            return "format_" + contentType.replace('/', '_');
        else
            return "undefined_format";
    }
}
