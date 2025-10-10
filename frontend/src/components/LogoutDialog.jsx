import {
  Dialog,
  Portal,
  Button,
  CloseButton,
  HStack,
  Text,
  Box,
  Icon,
} from "@chakra-ui/react";
import { LiaSignOutAltSolid } from "react-icons/lia";
import { useState } from "react";

/**
 * Dialog para confirmar cierre de sesión.
 * @param {object} props
 * @param {React.ReactNode} props.trigger - Elemento que dispara el dialog.
 * @param {function} props.onLogout - Función a ejecutar al confirmar logout.
 */
export function LogoutDialog({ trigger, onLogout }) {
  const [open, setOpen] = useState(false);

  const handleLogout = () => {
    if (onLogout) onLogout();
    setOpen(false);
  };

  return (
    <Dialog.Root open={open} onOpenChange={(d) => setOpen(d.open)}>
      <Dialog.Trigger asChild>
        <Box as="span" onClick={() => setOpen(true)}>
          {trigger}
        </Box>
      </Dialog.Trigger>
      <Portal>
        <Dialog.Backdrop bg="blackAlpha.400" />
        <Dialog.Positioner>
          <Dialog.Content
            maxW="400px"
            w="90%"
            borderRadius="md"
            shadow="lg"
            colorPalette="teal"
            bg="white"
            m={4}
          >
            <Dialog.Header
              p={4}
              borderBottomWidth="1px"
              borderColor="teal.600"
              bg="teal.700"
            >
              <HStack gap="2">
                <Icon
                  color="teal.400"
                  boxSize="5"
                  as={LiaSignOutAltSolid}
                />
                <Dialog.Title color="WhiteAlpha.900">
                  ¿Cerrar sesión?
                </Dialog.Title>
              </HStack>
            </Dialog.Header>
            <Dialog.Body p={4} bg="white">
              <Text fontSize="sm" color="gray.700">
                ¿Estás seguro que deseas cerrar tu cuenta y salir de la aplicación?
              </Text>
            </Dialog.Body>
            <Dialog.Footer p={4} bg="white" borderTopWidth="0">
              <HStack gap="2" justify="flex-end" w="100%">
                <Dialog.ActionTrigger asChild>
                  <Button
                    variant="outline"
                    size="sm"
                    colorPalette="teal"
                    px={2}
                  >
                    Cancelar
                  </Button>
                </Dialog.ActionTrigger>
                <Button
                  size="sm"
                  variant="solid"
                  colorPalette="teal"
                  onClick={handleLogout}
                  px={2}
                >
                  Cerrar sesión
                </Button>
              </HStack>
            </Dialog.Footer>
            <Dialog.CloseTrigger asChild>
              <CloseButton size="sm" />
            </Dialog.CloseTrigger>
          </Dialog.Content>
        </Dialog.Positioner>
      </Portal>
    </Dialog.Root>
  );
}