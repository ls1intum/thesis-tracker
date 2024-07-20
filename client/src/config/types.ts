export interface IGlobalConfig {
  title: string,

  api_server: string,

  focus_topics: Record<string, string>,
  research_areas: Record<string, string>,
  genders: Record<string, string>,
  study_programs: Record<string, string>,
  study_degrees: Record<string, string>,

  keycloak: {
    client_id: string,
    realm: string,
    url: string,
    get_unique_id: (decodedJwt: any) => string
  }
}
