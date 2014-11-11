using System;
using System.Linq;
using System.Net.Http;
using System.Threading.Tasks;
using System.Web;
using System.Windows;
using System.Windows.Navigation;
using Esri.ArcGISRuntime.Portal;
using Esri.ArcGISRuntime.Security;

namespace OAuth2
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        private readonly AgolAuthentication _agolAuthentication;
        private readonly AgolResults _agolResults;

        public MainWindow()
        {
            InitializeComponent();
            _agolResults = new AgolResults(this);
            _agolAuthentication = new AgolAuthentication(_agolResults);
            LoginBrowser.Source = _agolAuthentication.CreateLogInDialogUri();
            LoginBrowser.Navigated += LoginBrowserOnNavigated;
        }

        private void LoginBrowserOnNavigated(object sender, NavigationEventArgs dataTransferEventArgs)
        {
            var uri = dataTransferEventArgs.Uri;
            if (string.IsNullOrEmpty(uri.Query))
                return;

            var nameValueCollection = HttpUtility.ParseQueryString(uri.Query);

            if (nameValueCollection.AllKeys.Contains("code"))
            {
                var authorizationCode = nameValueCollection.GetValues("code")[0];
                _agolAuthentication.GetToken(authorizationCode);
            }
        }

        private void SearchButton_Click(object sender, RoutedEventArgs e)
        {
            _agolResults.SearchArcgisOnline();
        }

        private void SearchString_GotFocus(object sender, RoutedEventArgs routedEventArgs)
        {
            SearchString.Text = "";
        }

    }

}
