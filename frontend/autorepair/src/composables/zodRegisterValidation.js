import { z } from 'zod';

const passwordRules = (t) =>
  z
    .string()
    .min(8, { message: t('registerPage.validation.lengthAtLeast8') })
    .refine((v) => /[a-z]/.test(v), { message: t('registerPage.validation.passLowercase') })
    .refine((v) => /[A-Z]/.test(v), { message: t('registerPage.validation.passUppercase') })
    .refine((v) => /\d/.test(v), { message: t('registerPage.validation.passNumber') })
    .refine((v) => /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]+/.test(v), {
      message: t('registerPage.validation.passSpecialChar'),
    });


export const createUserSchema = (t, validationMode = 'register') => {
  return z
    .object({
      username: z.string().nonempty({ message: t('registerPage.validation.required') }),
      email: z.email({ message: t('registerPage.validation.invalidEmail') }),
      firstname: z
        .string()
        .min(2, { message: t('registerPage.validation.lengthAtLeast2') })
        .regex(/^\p{L}+$/u, { message: t('registerPage.validation.lettersOnly') }),
      lastname: z
        .string()
        .min(2, { message: t('registerPage.validation.lengthAtLeast2') })
        .regex(/^\p{L}+$/u, { message: t('registerPage.validation.lettersOnly') }),
      personalID: z
        .string()
        .length(8, { message: t('registerPage.validation.lengthExact8') })
        .regex(/^\p{L}{2}\d{6}$/u, { message: t('registerPage.validation.format2Letters6Digits') }),

      address: z.string().optional(),
      taxNumber: z.string().optional(),
      specialty: z.string().optional(),
      role: z.any(),

      password:
        validationMode === 'edit_profile'
          ? z.string().optional()
          : passwordRules(t),

      confirmPassword:
        validationMode === 'register'
          ? z.string().nonempty({ message: t('registerPage.validation.required') })
          : z.string().optional(),
    })
    .superRefine((data, ctx) => {
      if (validationMode === 'register') {
        if (data.password !== data.confirmPassword) {
          ctx.addIssue({
            code: 'custom',
            path: ['confirmPassword'],
            message: t('registerPage.validation.passwordsMismatch'),
          });
        }
      }

      const roleRaw = data.role?.name || data.role;
      const role = String(roleRaw).toLowerCase().trim();

      if (role === 'customer') {
        if (!data.address) {
          ctx.addIssue({ code: 'custom', path: ['address'], message: t('registerPage.validation.required') });
        } else if (data.address.length < 10) {
          ctx.addIssue({ code: 'custom', path: ['address'], message: t('registerPage.validation.addressTooShort') });
        }

        if (!data.taxNumber) {
          ctx.addIssue({
            code: 'custom',
            path: ['taxNumber'],
            message: t('registerPage.validation.required')
          });
        } else if (!/^\d+$/.test(data.taxNumber)) {
          // ✅ Check: Must contain only numbers
          ctx.addIssue({
            code: 'custom',
            path: ['taxNumber'],
            message: t('registerPage.validation.taxNumberDigitsOnly')
          });
        } else if (data.taxNumber.length !== 9) {
          // ✅ Check: Must be exactly 9 digits
          ctx.addIssue({
            code: 'custom',
            path: ['taxNumber'],
            message: t('registerPage.validation.taxNumberExact')
          });
        }
      }

      if (role === 'mechanic') {
        if (!data.specialty) {
          ctx.addIssue({ code: 'custom', path: ['specialty'], message: t('registerPage.validation.required') });
        } else if (data.specialty.length < 2) {
          ctx.addIssue({ code: 'custom', path: ['specialty'], message: t('registerPage.validation.lengthAtLeast2') });
        }
      }
    });
};