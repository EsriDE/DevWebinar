using System;
using Esri.ArcGISRuntime.Portal;

namespace OAuth2
{
    public class AgolResults
    {
        private MainWindow _mainWindow;
        private bool IsBusy;
        private ArcGISPortal _arcGisPortal;

        public AgolResults(MainWindow mainWindow)
        {
            _mainWindow = mainWindow;
        }

        public OAuth2Token ParseResult(string result, out Exception error)
        {
            OAuth2TokenResult results;
            try
            {
                using (var stream = new System.IO.MemoryStream(System.Text.Encoding.UTF8.GetBytes(result)))
                {
                    var serializer = new System.Runtime.Serialization.Json.DataContractJsonSerializer(typeof(OAuth2TokenResult));
                    results = serializer.ReadObject(stream) as OAuth2TokenResult;
                }
            }

            catch (Exception)
            {
                results = null;
            }
            var oAuth2Token = new OAuth2Token();

            error = null;

            if (results != null && results.AccessToken != null)
            {
                // Token returned --> no error
                oAuth2Token.Token = results.AccessToken;

                if (results.ExpiresIn != null)
                {
                    long expiresIn;
                    Int64.TryParse(results.ExpiresIn, out expiresIn);
                    oAuth2Token.ExpirationDate = DateTime.UtcNow + TimeSpan.FromSeconds(expiresIn);
                }

                if (results.RefreshToken != null)
                    oAuth2Token.RefreshToken = results.RefreshToken;
                if (results.Username != null)
                    oAuth2Token.UserName = results.Username;

                if (string.IsNullOrEmpty(oAuth2Token.Token))
                {
                    error = new Exception("Empty token generated");
                }
            }
            else
            {
                // Error
                string message = "No token generated";
                if (results != null)
                {
                    if (results.Error != null)
                    {
                        message += results.Error.Message;
                    }
                }
                error = new Exception(message);
            }
            return oAuth2Token;
        }

        public async void SearchArcgisOnline()
        {
            try
            {
                IsBusy = true;

                if (string.IsNullOrEmpty(_mainWindow.SearchString.Text) || _mainWindow.SearchString.Text.Length < 2)
                {
                    return;
                }

                UriBuilder dSU = new UriBuilder(AgolConsts.DEFAULT_SERVER_URL);

                _arcGisPortal = await ArcGISPortal.CreateAsync(dSU.Uri);

                var queryString = string.Format("{0} type:(\"web map\" NOT \"web mapping application\")", _mainWindow.SearchString.Text);

                if (_arcGisPortal.CurrentUser != null && _arcGisPortal.ArcGISPortalInfo != null && !string.IsNullOrEmpty(_arcGisPortal.ArcGISPortalInfo.Id))
                    queryString = string.Format("{0} orgid:(\"{1}\")", queryString, _arcGisPortal.ArcGISPortalInfo.Id);

                var searchParams = new SearchParameters(queryString)
                {
                    Limit = 20,
                    SortField = "avgrating",
                    SortOrder = QuerySortOrder.Descending,
                };
                var searchResults = await _arcGisPortal.SearchItemsAsync(searchParams);

                _mainWindow.SearchResultList.Items.Clear();
                foreach (var searchResult in searchResults.Results)
                {
                    _mainWindow.SearchResultList.Items.Add(searchResult.Title + " (" + searchResult.Owner + ", " + searchResult.Access + ")");
                }
            }
            finally
            {
                IsBusy = false;
            }
        }
    }
}