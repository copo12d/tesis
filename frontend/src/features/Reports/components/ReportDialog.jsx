import {
  Button,
  Icon,
  Text,
  Dialog,
  Portal,
  CloseButton,
  Stack,
  HStack,
} from "@chakra-ui/react";
import MenuSelect from "./MenuSelect";
import { HiSortAscending } from "react-icons/hi";
import { useState } from "react";

const sortByOptions = [
  { label: "ID", value: "id" },
  { label: "Nombre", value: "name" },
];
const sortDirOptions = [
  { label: "Ascendente", value: "ASC" },
  { label: "Descendente", value: "DESC" },
];

export default function ReportDialog({
  icon,
  iconColor,
  label,
  onDownload,
  sortBy: initialSortBy,
  setSortBy: setParentSortBy,
  sortDir: initialSortDir,
  setSortDir: setParentSortDir,
}) {
  const [open, setOpen] = useState(false);
  const [sortBy, setSortBy] = useState(initialSortBy);
  const [sortDir, setSortDir] = useState(initialSortDir);

  // Solo resetea cuando el dialog se abre
  const handleOpenChange = (isOpen) => {
    setOpen(isOpen);
    if (isOpen) {
      setSortBy(initialSortBy);
      setSortDir(initialSortDir);
    }
  };

  const handleDownload = async () => {
    await onDownload({ sortBy, sortDir });
    setParentSortBy(sortBy);
    setParentSortDir(sortDir);
    setOpen(false); // <-- Cierra el diálogo después de descargar
  };

  const handleCancel = () => {
    setOpen(false); // <-- Cierra el diálogo al cancelar
  };

  return (
    <Dialog.Root open={open} onOpenChange={handleOpenChange}>
      <Dialog.Trigger asChild>
        <Button
          w="100%"
          h="200px"
          borderRadius="xl"
          boxShadow="md"
          bgGradient="linear(to-r, #c6ea8d, #feffb8, #c6ea8d)"
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
                  icon={<HiSortAscending />}
                />
                <MenuSelect
                  label="Dirección de orden"
                  value={sortDir}
                  options={sortDirOptions}
                  onChange={setSortDir}
                  icon={<HiSortAscending />}
                />
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
              >
                Cancelar
              </Button>
              <Button colorPalette="teal" onClick={handleDownload}>
                Descargar
              </Button>
            </Dialog.Footer>
            <Dialog.CloseTrigger asChild>
              <CloseButton size="sm"/>
            </Dialog.CloseTrigger>
          </Dialog.Content>
        </Dialog.Positioner>
      </Portal>
    </Dialog.Root>
  );
}