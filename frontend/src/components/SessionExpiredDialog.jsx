import { useEffect, useState, useContext } from "react";
import {
  Dialog,
  Portal,
  Button,
  HStack,
  Text,
  Box,
  Icon,
} from "@chakra-ui/react";
import { LiaExclamationTriangleSolid } from "react-icons/lia";
import AuthContext from "../context/AuthContext";

export function SessionExpiredDialog({ isOpen }) {
  const { logout } = useContext(AuthContext);
  const [open, setOpen] = useState(isOpen);

  useEffect(() => {
    setOpen(isOpen);
  }, [isOpen]);

  useEffect(() => {
    if (open) {
      const timeout = setTimeout(() => {
        logout();
      }, 5000);
      return () => clearTimeout(timeout);
    }
  }, [open, logout]);

  return (
    <Dialog.Root
      open={open}
      onOpenChange={(d) => setOpen(d.open)}
      closeOnEscape={false}
      closeOnOverlayClick={false}
      role="alertdialog"
    >
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
                  as={LiaExclamationTriangleSolid}
                />
                <Text color="whiteAlpha.900" fontWeight="bold" fontSize="lg">
                  Sesión expirada
                </Text>
              </HStack>
            </Dialog.Header>

            <Dialog.Body p={4} bg="white">
              <Text fontSize="sm" color="gray.700">
                Su tiempo de sesión ha expirado. Será redirigido al login.
              </Text>
            </Dialog.Body>

            <Dialog.Footer p={4} bg="white" borderTopWidth="0">
              <HStack gap="2" justify="flex-end" w="100%">
                <Button
                  size="sm"
                  variant="solid"
                  colorPalette="teal"
                  onClick={logout}
                  px={4}
                >
                  OK
                </Button>
              </HStack>
            </Dialog.Footer>
          </Dialog.Content>
        </Dialog.Positioner>
      </Portal>
    </Dialog.Root>
  );
}
