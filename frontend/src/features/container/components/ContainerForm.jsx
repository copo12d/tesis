import {
  Stack,
  Button,
  Input,
  InputGroup,
  Field,
  Text,
  NativeSelect,
} from "@chakra-ui/react";
import {
  LiaBarcodeSolid,
  LiaMapMarkerAltSolid,
  LiaRulerCombinedSolid,
} from "react-icons/lia";
import { useContainerForm } from "../hooks/useContainerForm";
import { useContainerTypes } from "../hooks/useContainerTypes";

const STATUS_OPTIONS = [
  { value: "AVAILABLE", label: "Disponible" },
];

export function ContainerForm({
  loading = false,
  initialValues = {},
  onSubmit,
  submitText = "Guardar",
  title,
}) {
  const {
    form,
    errors,
    setField,
    handleSubmit,
  } = useContainerForm({ initialValues, onSubmit });

  const busy = loading;
  const isEdit = !!initialValues?.id;

  const iconAddonProps = { bg: "teal.700", px: 3 };

  // Hook para tipos de contenedor
  const { types: containerTypes, loading: loadingTypes } = useContainerTypes();

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

        {/* Serial */}
        <Field.Root required invalid={!!errors.serial}>
          <Field.Label color="black">Serial</Field.Label>
          <InputGroup startAddon={<LiaBarcodeSolid />} startAddonProps={iconAddonProps}>
            <Input
              type="text"
              placeholder="Serial del contenedor"
              value={form.serial}
              onChange={(e) => setField("serial", e.target.value)}
              size="lg"
              color="blackAlpha.900"
              isDisabled={busy}
            />
          </InputGroup>
          {errors.serial && <Field.ErrorText>{errors.serial}</Field.ErrorText>}
        </Field.Root>

        {/* Latitud */}
        <Field.Root required invalid={!!errors.latitude}>
          <Field.Label color="black">Latitud</Field.Label>
          <InputGroup startAddon={<LiaMapMarkerAltSolid />} startAddonProps={iconAddonProps}>
            <Input
              type="number"
              step="any"
              placeholder="Latitud"
              value={form.latitude ?? ""}
              onChange={(e) => setField("latitude", e.target.value)}
              size="lg"
              color="blackAlpha.900"
              disabled={busy}
            />
          </InputGroup>
          {errors.latitude && <Field.ErrorText>{errors.latitude}</Field.ErrorText>}
        </Field.Root>

        {/* Longitud */}
        <Field.Root required invalid={!!errors.longitude}>
          <Field.Label color="black">Longitud</Field.Label>
          <InputGroup startAddon={<LiaMapMarkerAltSolid />} startAddonProps={iconAddonProps}>
            <Input
              type="number"
              step="any"
              placeholder="Longitud"
              value={form.longitude ?? ""}
              onChange={(e) => setField("longitude", e.target.value)}
              size="lg"
              color="blackAlpha.900"
              disabled={busy}
            />
          </InputGroup>
          {errors.longitude && <Field.ErrorText>{errors.longitude}</Field.ErrorText>}
        </Field.Root>

        {/* Capacidad */}
        <Field.Root required invalid={!!errors.capacity}>
          <Field.Label color="black">Capacidad (L)</Field.Label>
          <InputGroup startAddon={<LiaRulerCombinedSolid />} startAddonProps={iconAddonProps}>
            <Input
              type="number"
              placeholder="Capacidad en litros"
              value={form.capacity}
              onChange={(e) => setField("capacity", e.target.value)}
              size="lg"
              color="blackAlpha.900"
              isDisabled={busy}
            />
          </InputGroup>
          {errors.capacity && <Field.ErrorText>{errors.capacity}</Field.ErrorText>}
        </Field.Root>

        {/* Estado */}
        {/* <Field.Root required invalid={!!errors.status}>
          <Field.Label color="black">Estado</Field.Label>
          <NativeSelect.Root size="lg">
            <NativeSelect.Field
              value={form.status}
              onChange={(e) => setField("status", e.target.value)}
              color="blackAlpha.900"
              disabled={busy}
            >
              {STATUS_OPTIONS.map((opt) => (
                <option key={opt.value} value={opt.value}>
                  {opt.label}
                </option>
              ))}
            </NativeSelect.Field>
            <NativeSelect.Indicator />
          </NativeSelect.Root>
          {errors.status && <Field.ErrorText>{errors.status}</Field.ErrorText>}
        </Field.Root> */}

        {/* Tipo de contenedor */}
        <Field.Root required invalid={!!errors.containerTypeId}>
          <Field.Label color="black">Tipo de contenedor</Field.Label>
          <NativeSelect.Root size="lg">
            <NativeSelect.Field
              value={form.containerTypeId}
              onChange={(e) => setField("containerTypeId", e.target.value)}
              color="blackAlpha.900"
              disabled={busy || loadingTypes}
            >
              {containerTypes.map((type) => (
                <option key={type.id} value={type.id} style={{ backgroundColor:"#fff" }} >
                  {type.name}
                </option>
              ))}
            </NativeSelect.Field>
            <NativeSelect.Indicator />
          </NativeSelect.Root>
          {errors.containerTypeId && (
            <Field.ErrorText>{errors.containerTypeId}</Field.ErrorText>
          )}
        </Field.Root>

        <Button
          type="submit"
          colorPalette="green"
          size="lg"
          loading={busy}
          loadingText="Guardando..."
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