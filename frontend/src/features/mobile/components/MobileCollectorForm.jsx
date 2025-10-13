import { useState, useEffect } from "react";
import {
  Stack,
  Button,
  Text,
  Field,
  NativeSelect,
  Input,
  InputGroup,
} from "@chakra-ui/react";
import { toast } from "react-hot-toast";

// Simulación: función para obtener los lotes (batch) activos
// Reemplaza esto por tu hook real cuando el endpoint esté listo
const useActiveBatches = () => {
  const [batches, setBatches] = useState([]);
  const [loading, setLoading] = useState(true);
  useEffect(() => {
    // Simula fetch
    setTimeout(() => {
      setBatches([
        { id: 1, description: "Lote de recolección 1" },
        { id: 2, description: "Lote de recolección 2" },
      ]);
      setLoading(false);
    }, 500);
  }, []);
  return { batches, loading };
};

export function MobileCollectorForm({ containerId, onSubmit, loading }) {
  const [form, setForm] = useState({
    weight: "",
    batchId: "",
  });
  const [errors, setErrors] = useState({});
  const { batches, loading: loadingBatches } = useActiveBatches();

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

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!validate()) return;
    // containerId viene por prop, no lo pone el usuario
    onSubmit({
      weight: form.weight,
      batchId: Number(form.batchId),
      containerId: Number(containerId),
    });
  };

  return (
    <form onSubmit={handleSubmit}>
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
              disabled={loading}
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
          <NativeSelect.Root size="lg">
            <NativeSelect.Field
              value={form.batchId}
              onChange={(e) => handleChange("batchId", e.target.value)}
              color="blackAlpha.900"
              disabled={loading || loadingBatches}
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
          {errors.batchId && (
            <Field.ErrorText>{errors.batchId}</Field.ErrorText>
          )}
        </Field.Root>

        {/* Botón */}
        <Button
          type="submit"
          colorScheme="teal"
          size="lg"
          loading={loading}
          loadingText="Reportando..."
          alignSelf="flex-end"
          disabled={loading || loadingBatches}
        >
          Reportar
        </Button>

        {Object.values(errors).some((msg) => !!msg) && (
          <Text fontSize="sm" color="red.500">
            Corrige los campos marcados.
          </Text>
        )}
      </Stack>
    </form>
  );
}