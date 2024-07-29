import React, { PropsWithChildren, useEffect, useMemo, useState } from 'react'
import {
  AuthenticationContext,
  IAuthenticationContext,
  IDecodedAccessToken,
  IDecodedRefreshToken,
} from './context'
import Keycloak from 'keycloak-js'
import { GLOBAL_CONFIG } from '../../config/global'
import { jwtDecode } from 'jwt-decode'
import { IUserInfo } from '../../requests/types/user'
import { getAuthenticationTokens, useAuthenticationTokens } from '../../hooks/authentication'
import { useSignal } from '../../hooks/utility'

interface IAuthenticationProviderProps {}

const keycloak = new Keycloak({
  realm: GLOBAL_CONFIG.keycloak.realm,
  url: GLOBAL_CONFIG.keycloak.host,
  clientId: GLOBAL_CONFIG.keycloak.client_id,
})

const AuthenticationProvider = (props: PropsWithChildren<IAuthenticationProviderProps>) => {
  const { children } = props

  const [user, setUser] = useState<IUserInfo>()
  const [authenticationTokens, setAuthenticationTokens] = useAuthenticationTokens()
  const {
    signal: readySignal,
    triggerSignal: triggerReadySignal,
    ref: { isTriggerred: isReady },
  } = useSignal()

  useEffect(() => {
    setUser(undefined)

    const refreshAccessToken = () => {
      keycloak
        .updateToken(60 * 5)
        .then((isSuccess) => {
          if (!isSuccess) {
            setAuthenticationTokens(undefined)
          }
        })
    }

    const storeTokens = () => {
      const refreshToken = keycloak.refreshToken
      const accessToken = keycloak.token

      const decodedAccessToken = accessToken
        ? jwtDecode<IDecodedAccessToken>(accessToken)
        : undefined
      const decodedRefreshToken = refreshToken
        ? jwtDecode<IDecodedRefreshToken>(refreshToken)
        : undefined

      console.log('decoded keycloak refresh token', decodedRefreshToken)
      console.log('decoded keycloak access token', decodedAccessToken)

      if (decodedRefreshToken?.exp) {
        console.log(`refresh token expires in ${Math.floor(decodedRefreshToken.exp - Date.now() / 1000)} seconds`)
      }

      // refresh if already expired
      if (decodedRefreshToken?.exp && decodedRefreshToken.exp <= Date.now() / 1000) {
        return setAuthenticationTokens(undefined)
      } else if (decodedAccessToken?.exp && decodedAccessToken.exp <= Date.now() / 1000) {
        return refreshAccessToken()
      }

      if (accessToken && refreshToken) {
        setAuthenticationTokens({
          access_token: accessToken,
          refresh_token: refreshToken,
        })
      } else {
        setAuthenticationTokens(undefined)
      }
    }

    const storedTokens = getAuthenticationTokens()

    keycloak.onTokenExpired = () => refreshAccessToken()
    keycloak.onAuthRefreshSuccess = () => storeTokens()

    console.log('Initializing keycloak...')

    void keycloak
      .init({
        refreshToken: storedTokens?.refresh_token,
        token: storedTokens?.access_token,
      })
      .then(() => {
        console.log('Keycloak initialized')

        storeTokens()
        triggerReadySignal()
      })
      .catch((error) => {
        console.log('Keycloak init error', error)
      })

    const refreshTokenFrequency = 60 * 1000;
    const refreshTokenInterval = setInterval(() => {
      const refreshToken = keycloak.refreshToken
      const accessToken = keycloak.token

      const decodedAccessToken = accessToken
        ? jwtDecode<IDecodedAccessToken>(accessToken)
        : undefined
      const decodedRefreshToken = refreshToken
        ? jwtDecode<IDecodedRefreshToken>(refreshToken)
        : undefined

      if (decodedRefreshToken?.exp && decodedRefreshToken.exp <= Date.now() / 1000) {
        keycloak.clearToken()

        return setAuthenticationTokens(undefined)
      } else if (decodedAccessToken?.exp && decodedAccessToken.exp <= Date.now() / 1000 + refreshTokenFrequency) {
        return refreshAccessToken()
      }
    }, refreshTokenFrequency)

    return () => {
      clearInterval(refreshTokenInterval)

      keycloak.onAuthRefreshSuccess = undefined
      keycloak.onTokenExpired = undefined
    }
  }, [])

  useEffect(() => {
    if (!isReady) {
      return
    }

    if (authenticationTokens?.access_token) {
      const decodedAccessToken = jwtDecode<IDecodedAccessToken>(authenticationTokens.access_token)

      setUser({
        first_name: decodedAccessToken.given_name,
        last_name: decodedAccessToken.family_name,
        email: decodedAccessToken.email,
        university_id: decodedAccessToken[GLOBAL_CONFIG.keycloak.university_id_jwt_field] || '',
        user_id: decodedAccessToken[GLOBAL_CONFIG.keycloak.university_id_jwt_field] || '',
        roles: decodedAccessToken.resource_access[GLOBAL_CONFIG.keycloak.client_id]?.roles ?? [],
      })
    } else {
      setUser(undefined)
    }
  }, [authenticationTokens?.access_token, isReady])

  const contextValue = useMemo<IAuthenticationContext>(() => {
    return {
      isAuthenticated: !!authenticationTokens?.access_token,
      user: authenticationTokens?.access_token ? user : undefined,
      groups: [],
      login: () =>
        readySignal.then(() => {
          !keycloak.authenticated && keycloak.login()
        }),
      logout: (redirectUri: string) =>
        readySignal.then(() => {
          setAuthenticationTokens(undefined)

          keycloak.authenticated &&
            keycloak.logout({
              redirectUri: `${location.origin}${redirectUri}`,
            })
        }),
    }
  }, [user, !!authenticationTokens?.access_token, location.origin])

  return (
    <AuthenticationContext.Provider value={contextValue}>{children}</AuthenticationContext.Provider>
  )
}

export default AuthenticationProvider