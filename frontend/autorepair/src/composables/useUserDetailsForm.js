// src/features/account/composables/useUserDetailsForm.js
import { computed } from 'vue';
import { z } from 'zod';
import { zodResolver } from '@primevue/forms/resolvers/zod';
import { useI18n } from 'vue-i18n';



export const getRoleIcon = (role) => {
  const r = (role || '').toUpperCase()
  switch (r) {
    case 'SECRETARY':
      return 'pi pi-clipboard'
    case 'MECHANIC':
      return 'pi pi-wrench'
    case 'CUSTOMER':
      return 'pi pi-car'
    default:
      return 'pi pi-user'
  }
};


export function useUserDetailsForm(userRef) {
  const { t } = useI18n();

  const initialValues = computed(() => {
    if (!userRef.value) return {};
    return {
      username: userRef.value.username ?? '',
      email: userRef.value.email ?? '',
      role: userRef.value.role ?? '',
      firstname: userRef.value.firstName ?? '',
      lastname: userRef.value.lastName ?? '',
      personalID: userRef.value.identityNumber ?? '',
      address: userRef.value.address ?? '',
      taxNumber: userRef.value.taxNumber ?? '',
      specialty: userRef.value.specialty ?? '',
    };
  });

  const infoItems = computed(() => {
    if (!userRef.value) return [];
    const base = [
      { name: 'username', label: t('accountPage.userDetails.username'), icon: 'pi pi-user', value: initialValues.value.username,autocomplete: 'username', },
      { name: 'email', label: t('accountPage.userDetails.email'), icon: 'pi pi-at', value: initialValues.value.email, autocomplete: 'email', },
      { name: 'firstname', label: t('accountPage.userDetails.firstname'), icon: 'pi pi-user', value: initialValues.value.firstname, autocomplete: 'given-name', },
      { name: 'lastname', label: t('accountPage.userDetails.lastname'), icon: 'pi pi-user', value: initialValues.value.lastname, autocomplete: 'family-name', },
      { name: 'personalID', label: t('accountPage.userDetails.personalID'), icon: 'pi pi-user-plus', value: initialValues.value.personalID, autocomplete: 'off', },
      { name: 'role', label: t('accountPage.userDetails.role'), icon: 'pi pi-user-plus', value: initialValues.value.role.toLocaleLowerCase().trim(),autocomplete: 'off', },
    ];

    if (userRef.value.role === 'CUSTOMER') {
      base.push(
        { name: 'taxNumber', label: t('accountPage.userDetails.taxNumber'), icon: 'pi pi-calculator', value: initialValues.value.taxNumber,autocomplete: 'off', },
        { name: 'address', label: t('accountPage.userDetails.address'), icon: 'pi pi-home', value: initialValues.value.address, autocomplete: 'street-address', },
      );
    }

    if (userRef.value.role === 'MECHANIC') {
      base.push({ name: 'specialty', label: t('accountPage.userDetails.specialty'), icon: 'pi pi-cog', value: initialValues.value.specialty,autocomplete: 'off' });
    }

    return base;
  });


  const formResolver = computed(() =>
    zodResolver(
      z
        .object({

          role: z.string().nonempty(),
          address: z.string().optional(),
          taxNumber: z.string().optional(),
          specialty: z.string().optional(),
          firstname: z
            .string()
            .min(2, {
              message: t(
                'registerPage.validation.lengthAtLeast2'
              ),
            })
            .regex(/^\p{L}+$/u, {
              message: t(
                'registerPage.validation.lettersOnly'
              ),
            }),
          lastname: z
            .string()
            .min(2, {
              message: t(
                'registerPage.validation.lengthAtLeast2'
              ),
            })
            .regex(/^\p{L}+$/u, {
              message: t(
                'registerPage.validation.lettersOnly'
              ),
            }),
          personalID: z
            .string()
            .length(8, {
              message: t(
                'registerPage.validation.lengthExact8'
              ),
            })
            .regex(/^\p{L}{2}\d{6}$/u, {
              message: t(
                'registerPage.validation.format2Letters6Digits'
              ),
            }),
          email: z.email({
            message: t(
              'registerPage.validation.invalidEmail'
            ),
          }),
          username: z.string().nonempty({
            message: t('registerPage.validation.required'),
          }),
        })
        .superRefine((data, ctx) => {

          const role = data.role?.toLowerCase()

          if (role === 'customer') {
            if (!data.address) {
              ctx.addIssue({
                code: 'custom',
                path: ['address'],
                message: t(
                  'registerPage.validation.required'
                ),
              })
            } else if (data.address.length < 10) {
              ctx.addIssue({
                code: 'custom',
                path: ['address'],
                message: t(
                  'registerPage.validation.addressTooShort'
                ),
              })
            }
            if (!data.taxNumber) {
              ctx.addIssue({
                code: 'custom',
                path: ['taxNumber'],
                message: t(
                  'registerPage.validation.required'
                ),
              })
            } else if (data.taxNumber.length !== 9) {
              ctx.addIssue({
                code: 'custom',
                path: ['taxNumber'],
                message: t(
                  'registerPage.validation.taxNumberExact'
                ),
              })
            }
          }

          if (role === 'mechanic') {
            if (!data.specialty) {
              ctx.addIssue({
                code: 'custom',
                path: ['specialty'],
                message: t(
                  'registerPage.validation.required'
                ),
              })
            } else if (data.specialty.length < 2) {
              ctx.addIssue({
                code: 'custom',
                path: ['specialty'],
                message: t(
                  'registerPage.validation.lengthAtLeast2'
                ),
              })
            }
          }
        })
    )
  )

  return {
    initialValues,
    infoItems,
    formResolver,
  };
}