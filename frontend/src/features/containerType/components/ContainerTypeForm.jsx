import React from "react";
import {
  Stack,
  Button,
  Input,
  InputGroup,
  Field,
  Text,
} from "@chakra-ui/react";
import { LiaTagSolid, LiaInfoCircleSolid } from "react-icons/lia";
import { useContainerTypeForm } from "../hooks/useContainerTypeForm";

export function ContainerTypeForm({
  initialValues = { name: "", description: "" },
  loading = false,
  onSubmit,
  submitText = "Guardar",
  title = "Registrar tipo de contenedor",
}) {
  const { form, errors, setField, handleSubmit } = useContainerTypeForm({
    initialValues,
    onSubmit,
  });

  const iconAddonProps = { bg: "teal.700", px: 3 };

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
              onChange={(e) => setField("name", e.target.value)}
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
              onChange={(e) => setField("description", e.target.value)}
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