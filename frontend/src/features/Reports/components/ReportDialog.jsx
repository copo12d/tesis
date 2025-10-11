import {
  Button,
  Icon,
  Text,
  Dialog,
  Portal,
  CloseButton,
  Stack,
  HStack,
  Spinner,
} from "@chakra-ui/react";
import MenuSelect from "./MenuSelect";
import { useState } from "react";

export default function ReportDialog({
  icon,
  iconColor,
  label,
  onDownload,
  loading = false, // <-- nuevo prop
  sortBy: initialSortBy,
  setSortBy: setParentSortBy,
  sortDir: initialSortDir,
  setSortDir: setParentSortDir,
  sortByOptions = [
    { label: "ID", value: "id" },
    { label: "Nombre", value: "name" },
  ],
  sortDirOptions = [
    { label: "Ascendente", value: "ASC" },
    { label: "Descendente", value: "DESC" },
  ],
  extraFilters = [],
  onExtraFilterChange = () => {},
}) {
  const [open, setOpen] = useState(false);
  const [sortBy, setSortBy] = useState(initialSortBy);
  const [sortDir, setSortDir] = useState(initialSortDir);
  const [extraFilterValues, setExtraFilterValues] = useState(
    extraFilters.reduce((acc, f) => ({ ...acc, [f.value]: f.default || "" }), {})
  );

  const handleOpenChange = (isOpen) => {
    setOpen(isOpen);
    if (isOpen) {
      setSortBy(initialSortBy);
      setSortDir(initialSortDir);
      setExtraFilterValues(
        extraFilters.reduce((acc, f) => ({ ...acc, [f.value]: f.default || "" }), {})
      );
    }
  };

  // Ahora espera la descarga y cierra solo si fue exitosa
  const handleDownload = async () => {
    const result = await onDownload({ sortBy, sortDir, ...extraFilterValues });
    setParentSortBy(sortBy);
    setParentSortDir(sortDir);
    onExtraFilterChange(extraFilterValues);
    if (result?.success !== false) setOpen(false); // solo cierra si no hubo error
  };

  const handleCancel = () => setOpen(false);

  const handleExtraFilterChange = (key, val) => {
    setExtraFilterValues((prev) => ({ ...prev, [key]: val }));
  };

  return (
    <Dialog.Root open={open} onOpenChange={handleOpenChange}>
      <Dialog.Trigger asChild>
        <Button
          w="100%"
          h="200px"
          borderRadius="xl"
          boxShadow="md"
          display="flex"
          flexDirection="column"
          alignItems="center"
          justifyContent="center"
          transition="box-shadow 0.2s"
          _hover={{ boxShadow: "xl", bg: "teal.100" }}
          p={8}
        >
          <Icon
            as={icon}
            boxSize={14}
            color={iconColor}
            mb={3}
            opacity={0.85}
          />
          <Text
            fontWeight="bold"
            fontSize="lg"
            mb={2}
            color="teal.800"
            letterSpacing="wide"
          >
            {label}
          </Text>
          <Text color="teal.700" fontSize="md" fontWeight="medium">
            Descargar
          </Text>
        </Button>
      </Dialog.Trigger>
      <Portal>
        <Dialog.Backdrop bg="blackAlpha.400" />
        <Dialog.Positioner>
          <Dialog.Content
            maxW="400px"
            w="90%"
            borderRadius="md"
            shadow="lg"
            bg="white"
            m={4}
          >
            <Dialog.Header
              p={4}
              borderBottomWidth="1px"
              borderColor="teal.600"
              bg="teal.700"
              borderTopRadius="md"
            >
              <HStack gap="2">
                <Dialog.Title color="WhiteAlpha.900" fontWeight="bold" fontSize="lg">
                  Opciones de descarga
                </Dialog.Title>
              </HStack>
            </Dialog.Header>
            <Dialog.Body p={4} bg="white" color={"blackAlpha.700"}>
              <Stack spacing={4}>
                <MenuSelect
                  label="Ordenar por"
                  value={sortBy}
                  options={sortByOptions}
                  onChange={setSortBy}
                  icon={sortByOptions.find(opt => opt.icon)?.icon}
                />
                <MenuSelect
                  label="Dirección de orden"
                  value={sortDir}
                  options={sortDirOptions}
                  onChange={setSortDir}
                  icon={sortDirOptions.find(opt => opt.icon)?.icon}
                />
                {extraFilters.map((filter) => (
                  <MenuSelect
                    key={filter.value}
                    label={filter.label}
                    value={extraFilterValues[filter.value]}
                    options={filter.options}
                    onChange={(val) => handleExtraFilterChange(filter.value, val)}
                    icon={filter.icon}
                  />
                ))}
              </Stack>
            </Dialog.Body>
            <Dialog.Footer
              p={4}
              bg="white"
              borderTopWidth="0"
              display="flex"
              justifyContent="flex-end"
              gap={2}
            >
              <Button
                variant="outline"
                colorPalette="teal"
                onClick={handleCancel}
                px={2}
                disabled={loading}
              >
                Cancelar
              </Button>
              <Button
                colorPalette="teal"
                onClick={handleDownload}
                px={2}
                loading={loading}
                loadingText="Descargando..."
                disabled={loading}
              >
                Descargar
              </Button>
            </Dialog.Footer>
            <Dialog.CloseTrigger asChild>
            </Dialog.CloseTrigger>
          </Dialog.Content>
        </Dialog.Positioner>
      </Portal>
    </Dialog.Root>
  );
}