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
        context.resolveEdge(classFormat, EdgeType.ACCESS_VAR_POS_PERM, resourceFormat);
        var edge = context.resolveEdge(resource, EdgeType.D_COMMON_VAR, resourceFormat);
        context.resolveEdge(noRolFormat, EdgeType.ACCESS_VAR_POS_PERM, edge);
    }

//    private Long resolveResource(String contentType, InputStream resourceStream) throws ScMemoryException, IOException {
//        String hash = DigestUtils.md5DigestAsHex(resourceStream);
//
//        ScNode node = context.resolveKeynode(hash, NodeType.CONST);
//        var nrelFormat = context.resolveKeynode("nrel_format", NodeType.CONST_NO_ROLE);
//        var format = context.resolveKeynode(contentType, NodeType.CONST);
//        var edge = context.resolveEdge(node, EdgeType.D_COMMON_VAR, format);
//        context.resolveEdge(nrelFormat, EdgeType.ACCESS_VAR_POS_PERM, edge);
//        return node.getAddress();
//    }

    private String toContentType(String contentType) {
        if (contentType != null)
            return "format_" + contentType.replace('/', '_');
        else
            return "undefined_format";
    }
}
