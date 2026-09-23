# Solut_Arquive — projeto Android (Capacitor)

Este projeto empacota a mesa de trabalho Solut_Arquive (arquivo único `www/index.html`)
como um app Android nativo usando [Capacitor](https://capacitorjs.com).

## Estrutura

```
solut-arquive-app/
├─ www/index.html              # o app (mesmo artefato publicado no chat)
├─ android/                    # projeto Android nativo gerado pelo Capacitor
├─ capacitor.config.ts         # appId, nome do app, pasta web
├─ package.json
└─ .github/workflows/android.yml   # workflow que compila o APK
```

- **App ID:** `com.fullsolucions.solutarquive`
- **Nome do app:** Solut_Arquive

## Gerar o APK automaticamente (GitHub Actions)

1. Suba esta pasta para um repositório no GitHub (mantendo a estrutura acima).
2. Vá em **Actions** → **Build Android APK** → **Run workflow** (ou apenas dê push
   na branch `main`/`master` — o workflow roda sozinho).
3. Quando o job terminar, baixe o APK em **Artifacts**:
   - `solut-arquive-debug-apk` — instalável direto no celular para testes.
   - `solut-arquive-release-unsigned-apk` — gerado só ao rodar manualmente
     ("Run workflow"); precisa ser **assinado** antes de publicar na Play Store
     (veja abaixo).

## Gerar o APK localmente (opcional)

Pré-requisitos: Node.js 18+, JDK 17+ e o Android SDK instalados.

```bash
npm install
npx cap sync android
cd android
./gradlew assembleDebug
# APK gerado em: android/app/build/outputs/apk/debug/app-debug.apk
```

## Assinar o APK de release (para publicar na Play Store)

O workflow gera um APK de release **não assinado**. Para assinar:

```bash
keytool -genkey -v -keystore solut-arquive.keystore -alias solutarquive \
  -keyalg RSA -keysize 2048 -validity 10000

apksigner sign --ks solut-arquive.keystore \
  --out app-release-signed.apk app-release-unsigned.apk
```

Guarde o keystore em local seguro — ele é necessário para toda atualização futura
do app na Play Store.

## Atualizando o app depois de editar `www/index.html`

Sempre que o arquivo `www/index.html` for alterado, rode `npx cap sync android`
(ou deixe o workflow do GitHub Actions fazer isso automaticamente a cada push)
antes de gerar um novo APK.
