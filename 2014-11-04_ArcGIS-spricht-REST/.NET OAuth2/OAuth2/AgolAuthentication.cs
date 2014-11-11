using System;
using System.Net.Http;
using System.Threading.Tasks;
using Esri.ArcGISRuntime.Security;

namespace OAuth2
{
    public class AgolAuthentication
    {
        private readonly AgolResults _agolResults;
        private readonly AgolAuthentication _agolAuthentication;

        public AgolAuthentication(AgolResults agolResults)
        {
            _agolResults = agolResults;
            InitializeIdentityManager();
        }

        public async void GetToken(string authorizationCode)
        {
            var tokenWebResponse = await AccessTheWebAsync(CreateTokenUri(authorizationCode));
            Exception error;
            var token = _agolResults.ParseResult(tokenWebResponse, out error);

            OAuth2TokenTokenReceived(token);
        }

        private void OAuth2TokenTokenReceived(OAuth2Token token)
        {
            if (token == null)
            {
                return;
            }
            UpdateIdentityManager(token);
        }

        private void InitializeIdentityManager()
        {
            var challengeHandler = new ChallengeHandler(Challenge);
            IdentityManager.Current.ChallengeHandler = challengeHandler;
        }

        private Task<Credential> Challenge(CredentialRequestInfo arg)
        {
            return Task.FromResult<Credential>(null);
        }

        private void UpdateIdentityManager(OAuth2Token token)
        {
            var arcGisTokenCredential = new ArcGISTokenCredential { Token = token.Token, ServiceUri = AgolConsts.DEFAULT_SERVER_URL };
            IdentityManager.Current.AddCredential(arcGisTokenCredential);
        }

        private async Task<string> AccessTheWebAsync(Uri uri)
        {
            var client = new HttpClient();

            Task<string> getStringTask = client.GetStringAsync(uri.AbsoluteUri);

            string urlContents = await getStringTask;
            return urlContents;
        }

        public Uri CreateLogInDialogUri()
        {
            var uriBuilder = new UriBuilder(AgolConsts.AGOL_OAUTH_URI);

            var query = string.Empty;
            query += string.Format("client_id={0}", AgolConsts.AGOL_APPID);
            query += string.Format("&response_type={0}", "code");
            query += string.Format("&redirect_uri={0}", AgolConsts.AGOL_REDIRECT_URI_STRING);
            uriBuilder.Query = query;

            return uriBuilder.Uri;
        }

        private Uri CreateTokenUri(string authorizationCode)
        {
            var uriBuilder = new UriBuilder(AgolConsts.AGOL_GET_TOKEN_URI);

            var query = string.Empty;
            query += string.Format("client_id={0}", AgolConsts.AGOL_APPID);
            query += string.Format("&grant_type={0}", "authorization_code");
            query += string.Format("&redirect_uri={0}", AgolConsts.AGOL_REDIRECT_URI_STRING);
            query += string.Format("&code={0}", authorizationCode);
            uriBuilder.Query = query;

            return uriBuilder.Uri;
        }
    }
}