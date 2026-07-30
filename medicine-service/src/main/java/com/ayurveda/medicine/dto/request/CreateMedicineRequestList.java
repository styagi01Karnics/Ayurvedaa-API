package com.ayurveda.medicine.dto.request;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

/**
 * Accepts either one medicine object or an array of medicines on POST /api/v1/medicines.
 */
@Getter
@Setter
@JsonDeserialize(using = CreateMedicineRequestList.Deserializer.class)
public class CreateMedicineRequestList {

    @NotEmpty(message = "At least one medicine is required")
    @Valid
    private List<CreateMedicineRequest> medicines;

    public static class Deserializer extends JsonDeserializer<CreateMedicineRequestList> {

        @Override
        public CreateMedicineRequestList deserialize(JsonParser parser, DeserializationContext context)
                throws IOException {
            ObjectMapper mapper = (ObjectMapper) parser.getCodec();
            JsonNode node = mapper.readTree(parser);

            CreateMedicineRequestList request = new CreateMedicineRequestList();
            if (node.isArray()) {
                request.setMedicines(
                        mapper.convertValue(node, new TypeReference<List<CreateMedicineRequest>>() {
                        }));
            } else if (node.has("medicines") && node.get("medicines").isArray()) {
                request.setMedicines(
                        mapper.convertValue(
                                node.get("medicines"),
                                new TypeReference<List<CreateMedicineRequest>>() {
                                }));
            } else {
                request.setMedicines(List.of(mapper.convertValue(node, CreateMedicineRequest.class)));
            }
            return request;
        }
    }

}
