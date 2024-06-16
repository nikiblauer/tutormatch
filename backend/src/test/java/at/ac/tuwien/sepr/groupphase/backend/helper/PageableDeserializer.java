package at.ac.tuwien.sepr.groupphase.backend.helper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.IOException;

public class PageableDeserializer extends JsonDeserializer<Pageable> {

    @Override
    public Pageable deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        int page = node.get("pageNumber").asInt();
        int size = node.get("pageSize").asInt();

        Sort sort = Sort.unsorted();
        if (node.has("sort") && node.get("sort").has("sorted") && node.get("sort").get("sorted").asBoolean()) {
            // Assuming sort fields can be processed here
            // Sort logic based on your requirements
        }

        return PageRequest.of(page, size, sort);
    }
}

