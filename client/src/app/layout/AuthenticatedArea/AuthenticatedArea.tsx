import React, { PropsWithChildren, Suspense, useEffect } from 'react'
import {
  ActionIcon,
  AppShell,
  Burger,
  Center, Divider,
  Group, Image,
  Loader,
  Space, Text,
  useMantineColorScheme,
} from '@mantine/core'
import * as classes from './AuthenticatedArea.module.css'
import { Link, useLocation } from 'react-router-dom'
import { useDisclosure } from '@mantine/hooks'
import { Moon, Scroll, SignOut, Sun } from 'phosphor-react'
import { useIsSmallerBreakpoint } from '../../../hooks/theme'
import { useAuthenticationContext } from '../../../hooks/authentication'
import Logo from '../../../static/logo'

export interface IAuthenticatedAreaProps {
  requireAuthentication?: boolean
  collapseNavigation?: boolean
  requiredGroups?: string[]
}

const links: Array<{
  link: string
  label: string
  icon: any
  roles: string[] | undefined
}> = [
  /*{ link: '/dashboard', label: 'Dashboard', icon: NewspaperClipping, roles: undefined },
  {
    link: '/submit-application/pick-topic',
    label: 'Submit Application',
    icon: PaperPlaneTilt,
    roles: undefined,
  },*/
  {
    link: '/management/thesis-applications',
    label: 'Review Applications',
    icon: Scroll,
    roles: ['admin', 'advisor', 'supervisor'],
  },
  /*{
    link: '/applications',
    label: 'Review Applications v2',
    icon: Scroll,
    roles: ['admin', 'advisor', 'supervisor'],
  },
  {
    link: '/topics/create',
    label: 'Create Topic',
    icon: FolderSimplePlus,
    roles: ['admin', 'advisor', 'supervisor'],
  },
  { link: '/theses', label: 'Thesis Overview', icon: Kanban, roles: ['admin', 'supervisor'] },*/
]

const SpinningLoader = () => (
  <Center className={classes.fullHeight}>
    <Loader />
  </Center>
)

const AuthenticatedArea = (props: PropsWithChildren<IAuthenticatedAreaProps>) => {
  const {
    children,
    requireAuthentication = true,
    collapseNavigation = false,
    requiredGroups,
  } = props

  const { colorScheme, toggleColorScheme } = useMantineColorScheme()
  const [opened, { toggle }] = useDisclosure()

  const location = useLocation()
  const showHeader = useIsSmallerBreakpoint('md') || collapseNavigation
  const auth = useAuthenticationContext()

  useEffect(() => {
    if (requireAuthentication && !auth.isAuthenticated) {
      auth.login()
    }
  }, [requireAuthentication, auth.isAuthenticated])

  if (!requireAuthentication && !auth.isAuthenticated) {
    return <>{children}</>
  }

  return (
    <AppShell
      header={{ collapsed: !showHeader, height: 60 }}
      navbar={{
        width: 300,
        breakpoint: 'md',
        collapsed: { mobile: !opened, desktop: !opened && collapseNavigation },
      }}
      padding='md'
    >
      <AppShell.Header>
        <Group h='100%' px='md'>
          <Burger
            opened={opened}
            onClick={toggle}
            hiddenFrom={collapseNavigation ? undefined : 'md'}
            size='md'
          />
        </Group>
      </AppShell.Header>

      <AppShell.Navbar p='md'>
        <AppShell.Section grow mb='md'>
          <Group preventGrowOverflow={false}>
            <Logo className={classes.logo} />
            <Text className={classes.siteName} fw='bold'>ThesisTrack</Text>
            <ActionIcon
              variant='outline'
              color={colorScheme === 'dark' ? 'yellow' : 'pale-purple'}
              onClick={() => toggleColorScheme()}
              title='Toggle color scheme'
              ml='auto'
            >
              {colorScheme === 'dark' ? <Sun size='1.1rem' /> : <Moon size='1.1rem' />}
            </ActionIcon>
          </Group>
          <Divider my='sm' />
          {links
            .filter(
              (item) => !item.roles || item.roles.some((role) => auth.user?.groups.includes(role)),
            )
            .map((item) => (
              <Link
                className={classes.link}
                data-active={location.pathname.startsWith(item.link) || undefined}
                key={item.label}
                to={item.link}
              >
                <item.icon className={classes.linkIcon} size={32} />
                <span>{item.label}</span>
              </Link>
            ))}
        </AppShell.Section>
        <AppShell.Section>
          {/*<Link
            to='/settings/my-information'
            className={classes.link}
            data-active={location.pathname.startsWith('/settings/my-information') || undefined}
          >
            <User className={classes.linkIcon} size={32} />
            <span>My Information</span>
          </Link>*/}
          <Link to='/logout' className={classes.link}>
            <SignOut className={classes.linkIcon} size={32} />
            <span>Logout</span>
          </Link>
        </AppShell.Section>
      </AppShell.Navbar>

      <AppShell.Main>
        {auth.user ? (
          <Suspense fallback={<SpinningLoader />}>
            {!requiredGroups || requiredGroups.some((role) => auth.user?.groups.includes(role)) ? (
              children
            ) : (
              <Center className={classes.fullHeight}>
                <h1>403 - Unauthorized</h1>
              </Center>
            )}
          </Suspense>
        ) : (
          <SpinningLoader />
        )}
      </AppShell.Main>
    </AppShell>
  )
}

export default AuthenticatedArea
