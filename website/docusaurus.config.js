module.exports = {
  title: 'E-SEAL Module',
  tagline: 'Signing & Validation of documents',
  url: 'https://argoeu.github.io',
//  baseUrl: '/',
  baseUrl: '/gr.grnet.eseal/',
  onBrokenLinks: 'throw',
  onBrokenMarkdownLinks: 'warn',
  favicon: 'img/grnet-favicon.png',
  organizationName: 'ARGOeu', // Usually your GitHub org/user name.
  projectName: 'gr.grnet.eseal', // Usually your repo name.
  themeConfig: {
    navbar: {
      title: 'E-SEAL',
      logo: {
        alt: 'GRNET',
        src: 'img/grnet-logo.png',
      },
      items: [
        {
          to: 'docs/',
          activeBasePath: 'docs',
          label: 'Docs',
          position: 'left',
        },
        //{to: 'blog', label: 'Blog', position: 'left'},
        {
          //href: 'https://github.com/facebook/docusaurus',
          //label: 'GitHub',
          //position: 'right',
        },
      ],
    },
    footer: {
      style: 'dark',
      logo: {
        alt: 'GRNET',
        src: 'img/grnet-logo.png',
        href: 'http://www.grnet.gr/',
      },
      copyright: `Copyright © ${new Date().getFullYear()} <a href="http://www.grnet.gr/"> GRNET </a>`,
    },
  },
  presets: [
    [
      '@docusaurus/preset-classic',
      {
        docs: {
          sidebarPath: require.resolve('./sidebars.js'),
          // Please change this to your repo.
          //editUrl:
          //  'https://github.com/facebook/docusaurus/edit/master/website/',
        },
        //blog: {
        //  showReadingTime: true,
        //  // Please change this to your repo.
        //  editUrl:
        //    'https://github.com/facebook/docusaurus/edit/master/website/blog/',
        //},
        theme: {
          customCss: require.resolve('./src/css/custom.css'),
        },
      },
    ],
  ],
};
