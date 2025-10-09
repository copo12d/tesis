import {
  Stack,
  Button,
  Input,
  InputGroup,
  Field,
  Text,
} from "@chakra-ui/react";
import { LiaTagSolid, LiaInfoCircleSolid } from "react-icons/lia";
import { useState, useEffect } from "react";

export function ContainerTypeForm({
  initialValues = { name: "", description: "" },
  loading = false,
  onSubmit,
  submitText = "Guardar",
  title = "Registrar tipo de contenedor",
}) {
  const [form, setForm] = useState(initialValues);
  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (
      form.name !== initialValues.name ||
      form.description !== initialValues.description
    ) {
      setForm(initialValues);
    }
    // eslint-disable-next-line
  }, [initialValues]);

  const iconAddonProps = { bg: "teal.700", px: 3 };

  const handleChange = (field, value) => {
    setForm((prev) => ({ ...prev, [field]: value }));
    setErrors((prev) => ({ ...prev, [field]: "" }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    let newErrors = {};
    if (!form.name) newErrors.name = "El nombre es obligatorio";
    if (!form.description) newErrors.description = "La descripción es obligatoria";
    setErrors(newErrors);
    if (Object.keys(newErrors).length === 0 && onSubmit) {
      await onSubmit(form);
    }
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
        <Text fontSize="2xl" fontWeight="bold" mb={2} color="black">
          {title}
        </Text>

        <Field.Root required invalid={!!errors.name}>
          <Field.Label color="black">Nombre</Field.Label>
          <InputGroup startAddon={<LiaTagSolid />} startAddonProps={iconAddonProps}>
            <Input
              type="text"
              placeholder="Ej: Papel, Plástico, Orgánicos..."
              value={form.name}
              onChange={(e) => handleChange("name", e.target.value)}
              size="lg"
              color="blackAlpha.900"
              disabled={loading}
            />
          </InputGroup>
          {errors.name && <Field.ErrorText>{errors.name}</Field.ErrorText>}
        </Field.Root>

        <Field.Root required invalid={!!errors.description}>
          <Field.Label color="black">Descripción</Field.Label>
          <InputGroup startAddon={<LiaInfoCircleSolid />} startAddonProps={iconAddonProps}>
            <Input
              type="text"
              placeholder="Describe el tipo de desecho"
              value={form.description}
              onChange={(e) => handleChange("description", e.target.value)}
              size="lg"
              color="blackAlpha.900"
              disabled={loading}
            />
          </InputGroup>
          {errors.description && <Field.ErrorText>{errors.description}</Field.ErrorText>}
        </Field.Root>

        <Button
          type="submit"
          colorPalette="green"
          size="lg"
          loading={loading}
          loadingText="Guardando..."
          spinnerPlacement="end"
          alignSelf="flex-end"
          disabled={loading}
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