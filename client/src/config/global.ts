import { IGlobalConfig } from './types'

const getEnvironmentVariable = <T = string>(key: string, useJson = false): T | undefined => {
  const value = process.env[key] || window.RUNTIME_ENVIRONMENT_VARIABLES?.[key]

  if (!value) {
    return undefined
  }

  try {
    return useJson ? (JSON.parse(value) as T) : (value as T)
  } catch {
    return undefined
  }
}

export const GLOBAL_CONFIG: IGlobalConfig = {
  title: getEnvironmentVariable('APPLICATION_TITLE') || 'Thesis Track',

  chair_name: getEnvironmentVariable('CHAIR_NAME') || 'Thesis Track',
  chair_url: getEnvironmentVariable('CHAIR_URL') || window.origin,

  genders: getEnvironmentVariable<Record<string, string>>('GENDERS', true) || {
    MALE: 'Male',
    FEMALE: 'Female',
    OTHER: 'Other',
    PREFER_NOT_TO_SAY: 'Prefer not to say',
  },

  study_degrees: getEnvironmentVariable<Record<string, string>>('STUDY_DEGREES', true) || {
    BACHELOR: 'Bachelor',
    MASTER: 'Master',
  },

  study_programs: getEnvironmentVariable<Record<string, string>>('STUDY_PROGRAMS', true) || {
    COMPUTER_SCIENCE: 'Computer Science',
    INFORMATION_SYSTEMS: 'Information Systems',
    GAMES_ENGINEERING: 'Games Engineering',
    MANAGEMENT_AND_TECHNOLOGY: 'Management and Technology',
    OTHER: 'Other',
  },

  thesis_types: getEnvironmentVariable<Record<string, string>>('THESIS_TYPES', true) || {
    BACHELOR: 'Bachelor Thesis',
    MASTER: 'Master Thesis',
    INTERDISCIPLINARY_PROJECT: 'Interdisciplinary Project',
    GUIDED_RESEARCH: 'Guided Research',
  },

  custom_data: getEnvironmentVariable<Record<string, string>>('CUSTOM_DATA', true) || {
    GITHUB: 'Github Profile',
  },

  privacy_text: getEnvironmentVariable('PRIVACY') || '',
  imprint_text: getEnvironmentVariable('IMPRINT') || '',

  default_supervisors: getEnvironmentVariable('DEFAULT_SUPERVISOR_UUID')?.split(';') || [],
  calendar_url: getEnvironmentVariable('CALDAV_URL') || '',
  server_host: getEnvironmentVariable('SERVER_HOST') || 'http://localhost:8080',

  keycloak: {
    host: getEnvironmentVariable('KEYCLOAK_HOST') || 'http://localhost:8081',
    realm: getEnvironmentVariable('KEYCLOAK_REALM_NAME') || 'thesis-track',
    client_id: getEnvironmentVariable('KEYCLOAK_CLIENT_ID') || 'thesis-track-app',
  },
}
