import { defineModule } from '@/core/module';

export const ${frontendModuleVarName} = defineModule({
  name: '${frontendModuleName}',
  components: {
    '${registeredComponent}': () => import('./views/${GeneratorInfo.businessName}/index.vue')
  }
});

export default ${frontendModuleVarName};
