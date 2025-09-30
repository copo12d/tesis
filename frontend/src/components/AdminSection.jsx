import { useState } from "react";
import {
  Box,
  Button,
  VStack,
  Portal,
  Popover,
} from "@chakra-ui/react";
import { FiShield } from "react-icons/fi";

export default function AdminSection({ user }) {
  const [open, setOpen] = useState(false);

  const isAdmin =
    user?.role === "ROLE_ADMIN" || user?.role === "ROLE_SUPERUSER";
  if (!isAdmin) return null;

  const userSubItems = [
    { label: "Listado" },
    { label: "Crear" },
    { label: "Importar" },
    { label: "Exportar" },
  ];
  
  const containerSubItems = [
    { label: "Contenedor 1" },
    { label: "Contenedor 2" },
    { label: "Importar" },
    { label: "Exportar" },
  ];
  
  const adminItems = [
    {
      type: "popover",
      label: "Usuarios ▸",
      subItems: userSubItems,
    },
    {
      type: "popover",
      label: "Contenedores",
      subItems: containerSubItems,
    },
    { label: "Logs" },
  ];

  const baseBtnProps = {
    variant: "ghost",
    justifyContent: "flex-start",
    color: "white",
    fontSize: "sm",
    _hover: { bg: "whiteAlpha.200" },
  };

  return (
    <Box mt={3}>
      <Button
        variant="ghost"
        justifyContent="flex-start"
        color="white"
        fontWeight="medium"
        onClick={() => setOpen(o => !o)}
        _hover={{ bg: "whiteAlpha.200", transform: "translateX(2px)" }}
        _active={{ bg: "whiteAlpha.300" }}
        transition="all 0.15s"
        h="42px"
        px={3}
        borderRadius="md"
        display="flex"
        gap={3}
      >
        <Box as={FiShield} boxSize={5} opacity={0.9} />
        Administración
        <Box ml="auto" fontSize="xs" opacity={0.7}>
          {open ? "−" : "+"}
        </Box>
      </Button>

      {open && (
        <VStack mt={1} pl={6} align="stretch" spacing={1}>
          {adminItems.map(item => {
            if (item.type === "popover") {
              return (
                <Popover.Root
                  key={item.label}
                  positioning={{
                    placement: "right-start",
                    offset: { mainAxis: 4, crossAxis: 0 },
                  }}
                >
                  <Popover.Trigger asChild>
                    <Button {...baseBtnProps}>{item.label}</Button>
                  </Popover.Trigger>
                  <Portal>
                    <Popover.Positioner>
                      <Popover.Content
                        bg="teal.800"
                        borderColor="teal.600"
                        minW="180px"
                        p={2}
                        borderRadius="md"
                      >
                        <VStack align="stretch" spacing={1}>
                          {item.subItems.map(sub => (
                            <Button
                              key={sub.label}
                              {...baseBtnProps}
                            >
                              {sub.label}
                            </Button>
                          ))}
                        </VStack>
                      </Popover.Content>
                    </Popover.Positioner>
                  </Portal>
                </Popover.Root>
              );
            }
            return (
              <Button key={item.label} {...baseBtnProps}>
                {item.label}
              </Button>
            );
          })}
        </VStack>
      )}
    </Box>
  );
}
