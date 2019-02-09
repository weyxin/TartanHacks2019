package com.example.tartanhacks2019;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track;

public class Tab3Fragment extends Fragment {

    private SpotifyAppRemote mSpotifyAppRemote;
    private TextView songName;
    private TextView songArtist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab3, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        songName = view.findViewById(R.id.tvTitle);
        songArtist = view.findViewById(R.id.tvArtist);
    }

    @Override
    public void onStart() {
        super.onStart();
        ConnectionParams connectionParams =
                new ConnectionParams.Builder("dcaef32df78944cc90fc69348956171f")
                        .setRedirectUri("http://example.com/callback/")
                        .showAuthView(true)
                        .build();
        SpotifyAppRemote.connect(getActivity(), connectionParams, new Connector.ConnectionListener() {
            @Override
            public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                mSpotifyAppRemote = spotifyAppRemote;
                Log.d("SpotifyFragment", "Connected to Spotify.");
                connected();
            }
            @Override
            public void onFailure(Throwable throwable) {
                Log.d("SpotifyFragment", throwable.getMessage(), throwable);
            }
        });
    }

    public void connected() {
        mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:37i9dQZF1DX2sUQwD7tbmL");
        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerState -> {
                    final Track track = playerState.track;
                    if (track != null) {
                        Log.d("MainActivity", track.name + " by " + track.artist.name);
                        songName.setText(track.name);
                        songArtist.setText(track.artist.name);
                    }
                });
    }

    @Override
    public void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }
}
