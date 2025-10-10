import { useState } from "react";
import {
  Dialog,
  Portal,
  Button,
  Stack,
  Text,
  Spinner,
  Image,
  AspectRatio,
  CloseButton,
} from "@chakra-ui/react";
import { LiaQrcodeSolid } from "react-icons/lia";
import { FiDownload } from "react-icons/fi";
import { useQrPreview } from "../hooks/useQrPreview";

export function QrPreviewDialog({ trigger, containerId, serial }) {
  const [isOpen, setIsOpen] = useState(false);
  const { qrBase64, loading, error, fetchQr } = useQrPreview(containerId);

  const handleOpen = () => {
    setIsOpen(true);
    fetchQr();
  };

  const handleClose = () => setIsOpen(false);

  const handleDownload = () => {
    if (!qrBase64) return;
    const link = document.createElement("a");
    link.href = `data:image/png;base64,${qrBase64}`;
    link.download = `qr-${serial || containerId}.png`;
    link.click();
  };

  return (
    <Dialog.Root
      open={isOpen}
      onOpenChange={(open) => {
        if (!open) handleClose();
      }}
    >
      <Dialog.Trigger asChild>
        <span onClick={handleOpen}>{trigger}</span>
      </Dialog.Trigger>
      <Portal>
        <Dialog.Backdrop />
        <Dialog.Positioner>
          <Dialog.Content
            bg="white"
            borderColor="teal.600"
            borderWidth={2}
            borderRadius="lg"
            boxShadow="lg"
            maxW="sm"
          >
            <Dialog.Body pt={4}>
              <Stack align="center" spacing={4}>
                <Stack direction="row" align="center" spacing={2}>
                  <LiaQrcodeSolid size={28} color="#319795" />
                  <Dialog.Title
                    fontWeight="bold"
                    fontSize="xl"
                    color="teal.700"
                  >
                    Código QR del contenedor
                  </Dialog.Title>
                </Stack>
                <Dialog.Description mb={2} color="gray.600">
                  Escanea este código QR para identificar el contenedor.
                </Dialog.Description>
                {loading ? (
                  <Spinner size="lg" color="teal.500" />
                ) : qrBase64 ? (
                  <AspectRatio
                    ratio={1}
                    w="220px"
                    rounded="md"
                    overflow="hidden"
                    bg="gray.50"
                  >
                    <Image
                      src={`data:image/png;base64,${qrBase64}`}
                      alt={`QR del contenedor ${serial}`}
                      border="1px solid"
                      borderColor="teal.200"
                    />
                  </AspectRatio>
                ) : (
                  <Text color="red.500">
                    {error || "No se pudo cargar el QR."}
                  </Text>
                )}
                <Stack direction="row" spacing={2} pt={2} mb={2}>
                  <Button
                    onClick={handleClose}
                    variant="outline"
                    colorPalette="green"
                    px={2}
                  >
                    Cerrar
                  </Button>
                  <Button
                    onClick={handleDownload}
                    colorPalette="teal"
                    variant="solid"
                    disabled={!qrBase64}
                    px={2}
                  >
                    <FiDownload />
                    Descargar
                  </Button>
                </Stack>
              </Stack>
            </Dialog.Body>
            <Dialog.CloseTrigger top="2" insetEnd="2" asChild>
              <CloseButton size="sm" onClick={handleClose} />
            </Dialog.CloseTrigger>
          </Dialog.Content>
        </Dialog.Positioner>
      </Portal>
    </Dialog.Root>
  );
}
