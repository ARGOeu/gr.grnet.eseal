module.exports = {
  title: 'E-SEAL Module',
  tagline: 'Signing validating documents',
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
      //links: [
        //{
        //  title: 'Docs',
        //  items: [
        //    {
        //      label: 'Style Guide',
        //      to: 'docs/',
        //    },
        //    //{
        //    //  label: 'Second Doc',
        //    //  to: 'docs/doc2/',
        //    //},
        //  ],
        //},
        //{
        //  title: 'Community',
        //  items: [
        //    {
        //      label: 'Stack Overflow',
        //      href: 'https://stackoverflow.com/questions/tagged/docusaurus',
        //    },
        //    {
        //      label: 'Discord',
        //      href: 'https://discordapp.com/invite/docusaurus',
        //    },
        //    {
        //      label: 'Twitter',
        //      href: 'https://twitter.com/docusaurus',
        //    },
        //  ],
        //},
        //{
        //  title: 'More',
        //  items: [
        //    //{
        //    //  label: 'Blog',
        //    //  to: 'blog',
        //    //},
        //    {
        //      label: 'GitHub',
        //      href: 'https://github.com/facebook/docusaurus',
        //    },
        //  ],
        //},
      //],
      copyright: `<img alt="grnet" src="img/grnet-logo.png" height="50px"> </a> <br /> Copyright © ${new Date().getFullYear()} <a href="http://www.grnet.gr/"> GRNET </a>`,
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
