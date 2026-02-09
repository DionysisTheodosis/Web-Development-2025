import { z } from 'zod'

export const createCarSchema = (t, mode = 'create') => {
  return z.object({
    ownerId:
      mode === 'edit'
        ? z.number().optional()
        : z.number({
            required_error: t('cars.validation.Required'),
          }),

    serialNumber: z
      .string()
      .min(5, t('cars.validation.Required'))
      .max(20)
      .regex(
        /^[A-Za-z0-9]+$/,
        t('cars.validation.serialNumberFormat')
      ),
    brand: z
      .string()
      .regex(
        /^\p{L}[\p{L}\s\-0-9']+$/u,
        t('cars.validation.brandFormat')
      ),
    model: z
      .string()
      .regex(
        /^[\p{L}0-9\s-]+$/u,
        t('cars.validation.modelFormat')
      ),
    carType: z.enum(['PASSENGER', 'TRUCK', 'BUS'], {
      errorMap: () => ({
        message: t('cars.validation.invalidType'),
      }),
    }),

    fuelType: z.enum(
      ['ELECTRIC', 'DIESEL', 'LPG', 'HYBRID'],
      {
        errorMap: () => ({
          message: t('cars.validation.invalidFuel'),
        }),
      }
    ),

    doorCount: z.number().int().min(2).max(8),
    wheelCount: z.number().int().min(3).max(10),

    productionDate: z
      .union([z.date(), z.string()])
      .refine((val) => !!val, {
        message: t('cars.validation.Required'),
      }),

    acquisitionYear: z
      .number()
      .min(1900, t('cars.validation.yearTooOld'))
      .max(
        new Date().getFullYear(),
        t('cars.validation.yearInFuture')
      )
      .nullable(),
  })
}
