# MovieThree - FrontEnd
```
프론트 쪽은 리팩토링이 진행이 많이 되지않아 코드가 좀 지저분 합니다.
graphql, useQuery을 사용하여 데이터 통신을 진행합니다.
npm 16.20.0 사용
react / typescript를 사용하여 제작 되었습니다.
```

## 사용된 Package.json
```
{
  "name": "threemovieweb",
  "version": "0.1.0",
  "private": true,
  "dependencies": {
    "@apollo/react-hooks": "^4.0.0",
    "@emotion/react": "^11.10.6",
    "@emotion/styled": "^11.10.6",
    "@mui/icons-material": "^5.11.11",
    "@mui/lab": "^5.0.0-alpha.124",
    "@mui/material": "^5.11.14",
    "@mui/x-date-pickers": "^6.2.0",
    "@testing-library/jest-dom": "^5.16.5",
    "@testing-library/react": "^13.4.0",
    "@testing-library/user-event": "^13.5.0",
    "@types/jest": "^27.5.2",
    "@types/node": "^16.18.11",
    "@types/qs": "^6.9.7",
    "@types/react": "^18.0.26",
    "@types/react-dom": "^18.0.10",
    "@types/react-router-dom": "^5.3.3",
    "apollo-boost": "^0.4.9",
    "axios": "^1.3.4",
    "dayjs": "^1.11.7",
    "eslint-plugin-unused-imports": "^2.0.0",
    "graphql": "^16.6.0",
    "prettier": "^2.8.2",
    "qs": "^6.11.1",
    "react": "^18.2.0",
    "react-cookie": "^4.1.1",
    "react-dom": "^18.2.0",
    "react-router-dom": "^6.6.2",
    "react-scripts": "5.0.1",
    "recoil": "^0.7.7",
    "sass": "^1.62.0",
    "styled-components": "^5.3.9",
    "styled-reset": "^4.4.5",
    "swiper": "^9.1.1",
    "typescript": "^4.9.4",
    "v6": "^0.0.0",
    "web-vitals": "^2.1.4",
    "yarn": "^1.22.19"
  },
  "scripts": {
    "start": "react-scripts start",
    "build": "react-scripts build",
    "test": "react-scripts test",
    "eject": "react-scripts eject"
  },
  "eslintConfig": {
    "extends": [
      "react-app",
      "react-app/jest"
    ]
  },
  "browserslist": {
    "production": [
      ">0.2%",
      "not dead",
      "not op_mini all"
    ],
    "development": [
      "last 1 chrome version",
      "last 1 firefox version",
      "last 1 safari version"
    ]
  },
  "devDependencies": {
    "@types/styled-components": "^5.1.26",
    "@typescript-eslint/eslint-plugin": "^5.48.0",
    "@typescript-eslint/parser": "^5.48.0",
    "eslint": "8.22.0",
    "eslint-config-airbnb": "^19.0.4",
    "eslint-config-prettier": "^8.6.0",
    "eslint-plugin-import": "^2.26.0",
    "eslint-plugin-jsx-a11y": "^6.6.1",
    "eslint-plugin-prettier": "^4.2.1",
    "eslint-plugin-react": "^7.31.11",
    "eslint-plugin-react-hooks": "^4.6.0"
  },
  "compilerOptions": {
    "typeRoots": [
      "node_modules/@types",
      "src/types"
    ]
  }
}

```

## eslint
```
module.exports = {
    parser: '@typescript-eslint/parser',
    plugins: ['@typescript-eslint', 'prettier', 'import', 'unused-imports'],
    extends: [
        'airbnb',
        'plugin:import/errors',
        'plugin:import/warnings',
        'plugin:prettier/recommended',
        'plugin:@typescript-eslint/recommended',
    ],
    rules: {
        camelcase: 'off',
        'linebreak-style': 0,
        'import/prefer-default-export': 0,
        'prettier/prettier': 0,
        'import/extensions': 0,
        'no-use-before-define': 0,
        'import/no-unresolved': 0,
        'import/no-extraneous-dependencies': 0, // 테스트 또는 개발환경을 구성하는 파일에서는 devDependency 사용을 허용
        'no-shadow': 0,
        'react/prop-types': 0,
        'react/jsx-filename-extension': [2, {extensions: ['.js', '.jsx', '.ts', '.tsx']}],
        'jsx-a11y/no-noninteractive-element-interactions': 0,
        'react/function-component-definition': [2, {namedComponents: 'arrow-function'}],
        "react/require-default-props": "warn",
        'import/order': [
            'error',
            {
                groups: ['builtin', 'external', 'internal', 'parent', 'sibling', 'index', 'object', 'type'],
                pathGroups: [
                    {
                        pattern: 'react',
                        group: 'builtin',
                        position: 'before',
                    },
                ],
                pathGroupsExcludedImportTypes: ['react'],
            },
        ],
        'no-unused-vars': 'off',
        'unused-imports/no-unused-imports': 'error',
        'unused-imports/no-unused-vars': [
            'warn',
            {vars: 'all', varsIgnorePattern: '^_', args: 'after-used', argsIgnorePattern: '^_'},
        ],
    },
};
```

## prettier
```
{
  "singleQuote": true,
  "semi": true,
  "useTabs": false,
  "tabWidth": 4,
  "trailingComma": "all",
  "printWidth": 120,
  "arrowParens": "always"
}
```
