import {
  Stack,
  Button,
  Input,
  InputGroup,
  Field,
  Text,
} from "@chakra-ui/react";
import { LiaFileAltSolid } from "react-icons/lia";
import { useState } from "react";

export function BatchForm({
  loading = false,
  initialValues = {},
  onSubmit,
  submitText = "Registrar lote",
  title = "Registrar nuevo lote",
}) {
  const [form, setForm] = useState({
    description: initialValues.description || "",
  });
  const [errors, setErrors] = useState({});

  const busy = loading;

  const setField = (name, value) => {
    setForm((prev) => ({ ...prev, [name]: value }));
    setErrors((prev) => ({ ...prev, [name]: undefined }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    // Validación simple
    let newErrors = {};
    if (!form.description || form.description.trim().length < 3) {
      newErrors.description = "La descripción es obligatoria";
    }
    setErrors(newErrors);
    if (Object.keys(newErrors).length > 0) return;
    onSubmit(form);
  };

  return (
    <form onSubmit={handleSubmit}>
      <Stack
       spacing={6}
        p={4}
        bg="whiteAlpha.900"
        boxShadow="md"
        w="100%"
        h={"100vh"}
      >
        {title && (
          <Text fontSize="2xl" fontWeight="bold" mb={2} color="black">
            {title}
          </Text>
        )}

        <Field.Root required invalid={!!errors.description}>
          <Field.Label color="black">Descripción</Field.Label>
          <InputGroup startAddon={<LiaFileAltSolid />} startAddonProps={{ bg: "teal.700", px: 3 }}>
            <Input
              type="text"
              placeholder="Descripción del lote"
              value={form.description}
              onChange={(e) => setField("description", e.target.value)}
              size="lg"
              color="blackAlpha.900"
              disabled={busy}
              autoComplete="off"
            />
          </InputGroup>
          {errors.description && (
            <Field.ErrorText>{errors.description}</Field.ErrorText>
          )}
        </Field.Root>

        <Button
          type="submit"
          colorPalette="green"
          size="lg"
          loading={busy}
          loadingText="Registrando..."
          spinnerPlacement="end"
          alignSelf="flex-end"
          disabled={busy}
          px={2}
        >
          {submitText}
        </Button>

        {Object.keys(errors).length > 0 && (
          <Text fontSize="sm" color="red.500">
            Corrige los campos marcados.
          </Text>
        )}
      </Stack>
    </form>
  );
}