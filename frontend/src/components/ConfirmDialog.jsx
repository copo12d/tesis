import React, { useState, useCallback, useEffect } from "react";
import {
  Dialog,
  Portal,
  Button,
  CloseButton,
  HStack,
  Text,
  Box,
  Icon,
  Spinner,
} from "@chakra-ui/react";
import { LiaExclamationTriangleSolid } from "react-icons/lia";

/**
 * Props de colores (todos con defaults actuales):
 * confirmColorPalette = "red"
 * contentColorPalette = "green"
 * descriptionColor (alias viejo colorText) = "gray.200"
 * headerBorderColor = "gray.200"
 * contentBg = "white"
 * headerBg (opcional)
 * footerBg (opcional)
 * backdropBg = "blackAlpha.400"
 * iconColor (por defecto usa confirmColorPalette.500)
 * cancelVariant = "outline"
 * cancelColorPalette = "gray"
 * confirmVariant = "solid"
 */
export function ConfirmDialog({
  trigger,
  title = "Confirmar",
  description,
  confirmText = "Aceptar",
  cancelText = "Cancelar",
  onConfirm,
  confirmColorPalette = "teal",           // Cambiado a teal
  loading = false,
  icon = <LiaExclamationTriangleSolid />,
  hideCloseButton = false,

  // Color antiguo (compat)
  colorText,
  descriptionColor = colorText || "gray.700", // gris oscuro

  // Nuevos props de estilo (defaults teal y grises)
  iconColor,
  contentColorPalette = "teal",           // Cambiado a teal
  contentBg = "white",                    // Fondo blanco
  headerBg = "teal.700",                  // Header teal oscuro
  headerBorderColor = "teal.600",         // Borde teal
  footerBg = "white",                     // Footer blanco
  backdropBg = "blackAlpha.400",
  titleColor = "WhiteAlpha.900",                // Título blanco
  cancelVariant = "outline",
  cancelColorPalette = "gray",
  cancelTextColor,
  confirmVariant = "solid",
}) {
  const [open, setOpen] = useState(false);

  const handleConfirm = useCallback(async () => {
    if (onConfirm) {
      const r = onConfirm();
      if (r instanceof Promise) {
        try {
          await r;
        } catch {
          /* silencioso */
        }
      }
    }
    if (!loading) setOpen(false);
  }, [onConfirm, loading]);

  useEffect(() => {
    // placeholder si quieres cerrar automáticamente al terminar
  }, [loading]);

  const resolvedIconColor = iconColor || `${confirmColorPalette}.500`;

  return (
    <Dialog.Root open={open} onOpenChange={(d) => setOpen(d.open)}>
      <Dialog.Trigger asChild>
        <Box
          as="span"
          onClick={() => setOpen(true)}
        >
          {trigger}
        </Box>
      </Dialog.Trigger>

      <Portal>
        <Dialog.Backdrop bg={backdropBg} />
        <Dialog.Positioner>
          <Dialog.Content
            maxW="400px"
            w="90%"
            borderRadius="md"
            shadow="lg"
            colorPalette={contentColorPalette}
            bg={contentBg}
            m={4}
          >
            <Dialog.Header
              p={4}
              borderBottomWidth="1px"
              borderColor={headerBorderColor}
              bg={headerBg}
            >
              <HStack gap="2">
                {icon && (
                  <Icon
                    color={resolvedIconColor}
                    boxSize="5"
                    as={() => icon}
                  />
                )}
                <Dialog.Title color={titleColor}>{title}</Dialog.Title>
              </HStack>
            </Dialog.Header>

            <Dialog.Body p={4} bg={contentBg}>
              {description && (
                <Text fontSize="sm" color={descriptionColor}>
                  {description}
                </Text>
              )}
            </Dialog.Body>

            <Dialog.Footer
              p={4}
              bg={footerBg}
              borderTopWidth="0"
            >
              <HStack gap="2" justify="flex-end" w="100%">
                <Dialog.ActionTrigger asChild>
                  <Button
                    variant={cancelVariant}
                    size="sm"
                    colorPalette={cancelColorPalette}
                    disabled={loading}
                    color={cancelTextColor}
                    px={2}
                  >
                    {cancelText}
                  </Button>
                </Dialog.ActionTrigger>
                <Button
                  size="sm"
                  variant={confirmVariant}
                  colorPalette={confirmColorPalette}
                  onClick={handleConfirm}
                  disabled={loading}
                  loading={loading}
                  loadingText="Procesando..."
                  spinnerPlacement="end"
                  px={2}
                >
                  {confirmText}
                </Button>
              </HStack>
            </Dialog.Footer>

            {!hideCloseButton && (
              <Dialog.CloseTrigger asChild>
                <CloseButton size="sm" />
              </Dialog.CloseTrigger>
            )}
          </Dialog.Content>
        </Dialog.Positioner>
      </Portal>
    </Dialog.Root>
  );
}
