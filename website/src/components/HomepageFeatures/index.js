import React from 'react';
import clsx from 'clsx';
import styles from './styles.module.css';

const FeatureList = [
  {
    title: 'Sing PDF documents',
    Svg: require('@site/static/img/undraw_terms.svg').default,
    description: (
      <>
        E-seal Module adds qualified e-sealing capabilities.
      </>
    ),
  },
  {
    title: 'Validate PDF documents',
    Svg: require('@site/static/img/undraw_file.svg').default,
    description: (
      <>
       Check if PDF documents contain qualified digital signatures.
      </>
    ),
  },
];

function Feature({Svg, title, description}) {
  return (
    <div className={clsx('col col--6')}>
      <div className="text--center">
        <Svg className={styles.featureSvg} role="img" />
      </div>
      <div className="text--center padding-horiz--md">
        <h3>{title}</h3>
        <p>{description}</p>
      </div>
    </div>
  );
}

export default function HomepageFeatures() {
  return (
    <section className={styles.features}>
      <div className="container">
        <div className="row">
          {FeatureList.map((props, idx) => (
            <Feature key={idx} {...props} />
          ))}
        </div>
      </div>
    </section>
  );
}
