import { useState } from "react";
import {
  Stack,
  Button,
  Text,
  Field,
  NativeSelect,
  Input,
  InputGroup,
  Center,
  Box,
  Spinner,
} from "@chakra-ui/react";
import { useParams, useNavigate } from "react-router-dom";
import { useRegisterWaste } from "../hooks/useRegisterWaste";
import { useInProgressBatches } from "../hooks/useInProgressBatches";
import { ConfirmDialog } from "@/components/ConfirmDialog";

export default function MobileCollectorPage() {
  // Obtén el containerId desde localStorage
  const containerId = localStorage.getItem("containerId");
  const [form, setForm] = useState({
    weight: "",
    batchId: "",
  });
  const [errors, setErrors] = useState({});
  const { batches, loading: loadingBatches } = useInProgressBatches();
  const { registerWaste, loading: loadingWaste } = useRegisterWaste();
  const navigate = useNavigate();

  // Validación simple
  const validate = () => {
    const e = {};
    if (!form.weight || isNaN(Number(form.weight)) || Number(form.weight) <= 0) {
      e.weight = "El peso es obligatorio y debe ser mayor a 0";
    }
    if (!form.batchId) {
      e.batchId = "Selecciona un lote";
    }
    setErrors(e);
    return Object.keys(e).length === 0;
  };

  const handleChange = (k, v) => {
    setForm((f) => ({ ...f, [k]: v }));
    setErrors((e) => ({ ...e, [k]: undefined }));
  };

  const handleSubmit = async () => {
    if (!validate()) return;
    const ok = await registerWaste({
      weight: form.weight,
      batchId: Number(form.batchId),
      containerId: Number(containerId),
    });
    if (ok) {
      navigate("/mobile/thanks");
    }
  };

  return (
    <Center minH="100vh" bg="#e6f4ea">
      <Box w="100%" maxW="400px">
        <Stack spacing={6} p={4} bg="whiteAlpha.900" boxShadow="md" w="100%">
          <Text fontSize="2xl" fontWeight="bold" mb={2} color="black">
            Reportar desecho
          </Text>

          {/* Peso */}
          <Field.Root required invalid={!!errors.weight}>
            <Field.Label color="black">Peso (kg)</Field.Label>
            <InputGroup>
              <Input
                type="number"
                step="0.01"
                min="0"
                placeholder="Ej: 15.00"
                value={form.weight}
                onChange={(e) => handleChange("weight", e.target.value)}
                size="lg"
                color="blackAlpha.900"
                disabled={loadingWaste}
                autoComplete="off"
              />
            </InputGroup>
            {errors.weight && (
              <Field.ErrorText>{errors.weight}</Field.ErrorText>
            )}
          </Field.Root>

          {/* Lote */}
          <Field.Root required invalid={!!errors.batchId}>
            <Field.Label color="black">Lote</Field.Label>
            {loadingBatches ? (
              <Spinner color="teal.500" />
            ) : (
              <NativeSelect.Root size="lg">
                <NativeSelect.Field
                  value={form.batchId}
                  onChange={(e) => handleChange("batchId", e.target.value)}
                  color="blackAlpha.900"
                  disabled={loadingWaste || loadingBatches}
                >
                  <option value="">Selecciona un lote</option>
                  {batches.map((b) => (
                    <option key={b.id} value={b.id}>
                      {b.description}
                    </option>
                  ))}
                </NativeSelect.Field>
                <NativeSelect.Indicator />
              </NativeSelect.Root>
            )}
            {errors.batchId && (
              <Field.ErrorText>{errors.batchId}</Field.ErrorText>
            )}
          </Field.Root>

          {/* Botón con confirmación */}
          <ConfirmDialog
            trigger={
              <Button
                type="button"
                colorPalette="teal"
                size="lg"
                loading={loadingWaste}
                loadingText="Reportando..."
                alignSelf="center"
                disabled={loadingWaste || loadingBatches}
                px={"152px"}
                w="auto"
              >
                Reportar
              </Button>
            }
            title="Confirmar reporte"
            description="¿Está seguro que desea reportar este desecho?"
            confirmText="Reportar"
            cancelText="Cancelar"
            onConfirm={handleSubmit}
            loading={loadingWaste}
          />

          {Object.values(errors).some((msg) => !!msg) && (
            <Text fontSize="sm" color="red.500">
              Corrige los campos marcados.
            </Text>
          )}
        </Stack>
      </Box>
    </Center>
  );
}