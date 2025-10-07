import { useState } from "react";
import {
  Box,
  Button,
  VStack,
  Portal,
  Popover,
} from "@chakra-ui/react";
import { FiShield, FiUsers, FiBox, FiDownload, FiUpload, FiList, FiFileText } from "react-icons/fi";
import { TbRecycle, TbListDetails } from "react-icons/tb";
import { useNavigate } from "react-router-dom";

export default function AdminSection({ user }) {
  const [open, setOpen] = useState(false);
  const navigate = useNavigate();

  const isAdmin =
    user?.role === "ROLE_ADMIN" || user?.role === "ROLE_SUPERUSER";
  if (!isAdmin) return null;

  const userSubItems = [
    { label: "Listado", icon: FiList, action: () => navigate("/users/all") },
    { label: "Crear", icon: FiUsers, action: () => navigate("/users/new") },
    { label: "Reportes", icon: FiDownload },
  ];

  const containerSubItems = [
    { label: "Lista de tipos", icon: TbListDetails, action: () => navigate("/container-type/list") },
    { label: "Tipo de contenedor", icon: TbRecycle, action: () => navigate("/container-type/new") },
    { label: "Contenedor 1", icon: FiBox },
    { label: "Contenedor 2", icon: FiBox },
    { label: "Reportes", icon: FiUpload },
  ];

  const adminItems = [
    {
      type: "popover",
      label: "Usuarios ▸",
      icon: FiUsers,
      subItems: userSubItems,
    },
    {
      type: "popover",
      label: "Contenedores",
      icon: FiBox,
      subItems: containerSubItems,
    },
    { label: "Logs", icon: FiFileText },
  ];

  const baseBtnProps = {
    variant: "ghost",
    justifyContent: "flex-start",
    color: "white",
    fontSize: "sm",
    _hover: { bg: "whiteAlpha.200" },
    display: "flex",
    gap: 2,
    alignItems: "center",
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
              const ItemIcon = item.icon;
              return (
                <Popover.Root
                  key={item.label}
                  positioning={{
                    placement: "right-start",
                    offset: { mainAxis: 4, crossAxis: 0 },
                  }}
                >
                  <Popover.Trigger asChild>
                    <Button {...baseBtnProps}>
                      {ItemIcon && <Box as={ItemIcon} boxSize={4} opacity={0.9} />}
                      {item.label}
                    </Button>
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
                          {item.subItems.map(sub => {
                            const SubIcon = sub.icon;
                            return (
                              <Button
                                key={sub.label}
                                {...baseBtnProps}
                                onClick={() => {
                                  if (sub.action) {
                                    sub.action();
                                    setOpen(false); // cerrar solo si navega
                                  }
                                }}
                              >
                                {SubIcon && <Box as={SubIcon} boxSize={4} opacity={0.9} />}
                                {sub.label}
                              </Button>
                            );
                          })}
                        </VStack>
                      </Popover.Content>
                    </Popover.Positioner>
                  </Portal>
                </Popover.Root>
              );
            }
            const ItemIcon = item.icon;
            return (
              <Button key={item.label} {...baseBtnProps}>
                {ItemIcon && <Box as={ItemIcon} boxSize={4} opacity={0.9} />}
                {item.label}
              </Button>
            );
          })}
        </VStack>
      )}
    </Box>
  );
}
