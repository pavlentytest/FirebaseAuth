package ru.samsung.itschool.mdev.authfirebase;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ru.samsung.itschool.mdev.authfirebase.databinding.FragmentEmailPasswordBinding;

public class EmailPasswordFragment extends BaseFragment {

    private static final String TAG = "EmailPassword";

    private FragmentEmailPasswordBinding mBinding;

    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentEmailPasswordBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setProgressBar(mBinding.progressBar);
        // Кнопки
        mBinding.emailSignInButton.setOnClickListener(v -> {
            String email = mBinding.fieldEmail.getText().toString();
            String password = mBinding.fieldPassword.getText().toString();
            signIn(email, password);
        });
        mBinding.verifyEmailButton.setOnClickListener(v -> sendEmailVerification());
        mBinding.emailCreateAccountButton.setOnClickListener(v -> {
            String email = mBinding.fieldEmail.getText().toString();
            String password = mBinding.fieldPassword.getText().toString();
            createAccount(email, password);
        });
        mBinding.signOutButton.setOnClickListener(v -> signOut());
        mBinding.reloadButton.setOnClickListener(v -> reload());
        // Инициализация авторизации через FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Проверяем если пользователь авторизаован, то обновляем UI
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            reload();
        }
    }
    // Создание нового пользователя
    private void createAccount(String email, String password) {
        if (!validateForm()) {
            return;
        }
        showProgressBar();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Авторизация прошла успешно. Обновляем UI
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        // Авторизация прошла с ошибкой. Показываем ее в Toast.
                        Toast.makeText(getContext(), "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                    hideProgressBar();
                });
    }
    // Вход уже существующего пользователя
    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }
        showProgressBar();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Авторизация прошла успешно. Обновляем UI
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        // Авторизация прошла с ошибкой. Показываем ее в Toast.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(getContext(), "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }

                    if (!task.isSuccessful()) {
                        mBinding.status.setText(R.string.auth_failed);
                    }
                    hideProgressBar();
                });
    }
    // Метод для отправки подтверждающего письма для входа. Необходима доп.настройка в Firebase.
    private void sendEmailVerification() {
        mBinding.verifyEmailButton.setEnabled(false);
        // Отправка подтверждающего письма
        final FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        user.sendEmailVerification()
                .addOnCompleteListener(requireActivity(), task -> {
                    mBinding.verifyEmailButton.setEnabled(true);
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(),
                                "Verification email sent to " + user.getEmail(),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "sendEmailVerification", task.getException());
                        Toast.makeText(getContext(),
                                "Failed to send verification email.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void signOut() {
        mAuth.signOut();
        updateUI(null);
    }
    private void reload() {
        mAuth.getCurrentUser().reload().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                updateUI(mAuth.getCurrentUser());
                Toast.makeText(getContext(),
                        "Reload successful!",
                        Toast.LENGTH_SHORT).show();
            } else {
                Log.e(TAG, "reload", task.getException());
                Toast.makeText(getContext(),
                        "Failed to reload user.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    // Метод проверки заполненности полей
    private boolean validateForm() {
        boolean valid = true;

        String email = mBinding.fieldEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mBinding.fieldEmail.setError("Required.");
            valid = false;
        } else {
            mBinding.fieldEmail.setError(null);
        }

        String password = mBinding.fieldPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mBinding.fieldPassword.setError("Required.");
            valid = false;
        } else {
            mBinding.fieldPassword.setError(null);
        }

        return valid;
    }
    // Метод обновление UI
    private void updateUI(FirebaseUser user) {
        hideProgressBar();
        if (user != null) {
            mBinding.status.setText(getString(R.string.emailpassword_status_fmt,
                    user.getEmail(), user.isEmailVerified()));
            mBinding.detail.setText(getString(R.string.firebase_status_fmt, user.getUid()));

            mBinding.emailPasswordButtons.setVisibility(View.GONE);
            mBinding.emailPasswordFields.setVisibility(View.GONE);
            mBinding.signedInButtons.setVisibility(View.VISIBLE);

            if (user.isEmailVerified()) {
                mBinding.verifyEmailButton.setVisibility(View.GONE);
            } else {
                mBinding.verifyEmailButton.setVisibility(View.VISIBLE);
            }
        } else {
            mBinding.status.setText(R.string.signed_out);
            mBinding.detail.setText(null);

            mBinding.emailPasswordButtons.setVisibility(View.VISIBLE);
            mBinding.emailPasswordFields.setVisibility(View.VISIBLE);
            mBinding.signedInButtons.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }
}
